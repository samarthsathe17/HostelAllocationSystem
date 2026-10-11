package com.hostel.dao;

import com.hostel.model.AllocationRequest;
import com.hostel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllocationRequestDAO {

    public void addRequest(AllocationRequest request) throws SQLException {
        String sql = "INSERT INTO allocation_requests (student_id, status, priority_score, is_eligible, eligibility_details, recommended_room) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, request.getStudentId());
            pstmt.setString(2, request.getStatus());
            pstmt.setDouble(3, request.getPriorityScore());
            pstmt.setBoolean(4, request.isEligible());
            pstmt.setString(5, request.getEligibilityDetails());
            pstmt.setString(6, request.getRecommendedRoom());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setRequestId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<AllocationRequest> getAllRequests() throws SQLException {
        List<AllocationRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM allocation_requests ORDER BY priority_score DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AllocationRequest req = new AllocationRequest();
                req.setRequestId(rs.getInt("request_id"));
                req.setStudentId(rs.getInt("student_id"));
                req.setRequestDate(rs.getTimestamp("request_date"));
                req.setStatus(rs.getString("status"));
                req.setPriorityScore(rs.getDouble("priority_score"));
                req.setEligible(rs.getBoolean("is_eligible"));
                req.setEligibilityDetails(rs.getString("eligibility_details"));
                req.setRecommendedRoom(rs.getString("recommended_room"));
                requests.add(req);
            }
        }
        return requests;
    }

    public AllocationRequest getRequestById(int id) throws SQLException {
        String sql = "SELECT * FROM allocation_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    AllocationRequest req = new AllocationRequest();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setStudentId(rs.getInt("student_id"));
                    req.setRequestDate(rs.getTimestamp("request_date"));
                    req.setStatus(rs.getString("status"));
                    req.setPriorityScore(rs.getDouble("priority_score"));
                    req.setEligible(rs.getBoolean("is_eligible"));
                    req.setEligibilityDetails(rs.getString("eligibility_details"));
                    req.setRecommendedRoom(rs.getString("recommended_room"));
                    return req;
                }
            }
        }
        return null;
    }

    public void updateRequestStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE allocation_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }
}
