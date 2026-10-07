package com.hostel.dao;

import com.hostel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {

    public static class PendingDue {
        public int billId;
        public String studentName;
        public String rollNo;
        public double amount;
        public Date dueDate;

        public PendingDue(int billId, String studentName, String rollNo, double amount, Date dueDate) {
            this.billId = billId;
            this.studentName = studentName;
            this.rollNo = rollNo;
            this.amount = amount;
            this.dueDate = dueDate;
        }
    }

    public List<PendingDue> getPendingDues() throws SQLException {
        List<PendingDue> dues = new ArrayList<>();
        String sql = "SELECT d.bill_id, s.name, s.roll_no, d.amount, d.due_date " +
                     "FROM dues_and_billing d " +
                     "JOIN students s ON d.student_id = s.id " +
                     "WHERE d.payment_status = 'PENDING' ORDER BY d.due_date";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dues.add(new PendingDue(
                        rs.getInt("bill_id"),
                        rs.getString("name"),
                        rs.getString("roll_no"),
                        rs.getDouble("amount"),
                        rs.getDate("due_date")
                ));
            }
        }
        return dues;
    }

    public void clearDue(int billId) throws SQLException {
        String sql = "UPDATE dues_and_billing SET payment_status = 'PAID' WHERE bill_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            pstmt.executeUpdate();
        }
    }
}
