package com.hostel.dao;

import com.hostel.exception.DuplicateAllocationException;
import com.hostel.exception.PaymentPendingException;
import com.hostel.exception.RoomFullException;
import com.hostel.util.DatabaseConnection;

import java.sql.*;

public class AllocationDAO {

    public void allocateRoom(int studentId, String roomNo, String messPlan) throws SQLException, RoomFullException, DuplicateAllocationException, PaymentPendingException {
        Connection conn = DatabaseConnection.getConnection();
        
        try {
            conn.setAutoCommit(false); // Start transaction

            // 1. Check for duplicate active allocation
            checkDuplicateAllocation(conn, studentId);

            // 2. Check for pending dues
            checkPendingDues(conn, studentId);

            // 3. Lock room row and check vacancy
            checkRoomVacancy(conn, roomNo);

            // 4. Insert allocation
            insertAllocation(conn, studentId, roomNo);

            // 5. Update room occupancy
            updateRoomOccupancy(conn, roomNo);
            
            // 6. Insert Mess Subscription
            insertMessSubscription(conn, studentId, messPlan);

            conn.commit(); // Commit transaction
        } catch (Exception e) {
            conn.rollback(); // Rollback on failure
            throw e;
        } finally {
            conn.setAutoCommit(true); // Reset auto-commit
        }
    }

    private void checkDuplicateAllocation(Connection conn, int studentId) throws SQLException, DuplicateAllocationException {
        String sql = "SELECT COUNT(*) FROM allocations WHERE student_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DuplicateAllocationException("Student already has an active room allocation.");
                }
            }
        }
    }

    private void checkPendingDues(Connection conn, int studentId) throws SQLException, PaymentPendingException {
        String sql = "SELECT SUM(amount) FROM dues_and_billing WHERE student_id = ? AND payment_status = 'PENDING'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getDouble(1) > 0) {
                    throw new PaymentPendingException("Student has pending dues: $" + rs.getDouble(1) + ". Clear dues before new allocation.");
                }
            }
        }
    }

    private void checkRoomVacancy(Connection conn, String roomNo) throws SQLException, RoomFullException {
        String sql = "SELECT capacity, current_occupancy FROM rooms WHERE room_no = ? FOR UPDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int capacity = rs.getInt("capacity");
                    int currentOccupancy = rs.getInt("current_occupancy");
                    if (currentOccupancy >= capacity) {
                        throw new RoomFullException("Room " + roomNo + " is already full.");
                    }
                } else {
                    throw new SQLException("Room " + roomNo + " does not exist.");
                }
            }
        }
    }

    private void insertAllocation(Connection conn, int studentId, String roomNo) throws SQLException {
        String sql = "INSERT INTO allocations (student_id, room_no, allocation_date) VALUES (?, ?, CURDATE())";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, roomNo);
            pstmt.executeUpdate();
        }
    }

    private void updateRoomOccupancy(Connection conn, String roomNo) throws SQLException {
        String sql = "UPDATE rooms SET current_occupancy = current_occupancy + 1 WHERE room_no = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNo);
            pstmt.executeUpdate();
        }
    }
    
    private void insertMessSubscription(Connection conn, int studentId, String planType) throws SQLException {
        double monthlyFee = switch (planType) {
            case "PREMIUM" -> 3000.00;
            case "VEG_ONLY" -> 2000.00;
            default -> 2500.00; // STANDARD
        };
        
        String sql = "INSERT INTO mess_subscriptions (student_id, plan_type, monthly_fee) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, planType);
            pstmt.setDouble(3, monthlyFee);
            pstmt.executeUpdate();
        }
    }
}
