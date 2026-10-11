package com.hostel.dao;

import com.hostel.model.AllocationSettings;
import com.hostel.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO {
    public AllocationSettings getSettings() throws SQLException {
        String sql = "SELECT * FROM allocation_settings WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                AllocationSettings settings = new AllocationSettings();
                settings.setId(rs.getInt("id"));
                settings.setMinCgpa(rs.getDouble("min_cgpa"));
                settings.setMinAttendance(rs.getDouble("min_attendance"));
                settings.setWeightCgpa(rs.getInt("weight_cgpa"));
                settings.setWeightAttendance(rs.getInt("weight_attendance"));
                settings.setWeightDistance(rs.getInt("weight_distance"));
                settings.setWeightFinancial(rs.getInt("weight_financial"));
                settings.setWeightYear(rs.getInt("weight_year"));
                return settings;
            }
        }
        return null; // Should not happen if seeded
    }

    public void updateSettings(AllocationSettings settings) throws SQLException {
        String sql = "UPDATE allocation_settings SET min_cgpa = ?, min_attendance = ?, weight_cgpa = ?, weight_attendance = ?, weight_distance = ?, weight_financial = ?, weight_year = ? WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, settings.getMinCgpa());
            pstmt.setDouble(2, settings.getMinAttendance());
            pstmt.setInt(3, settings.getWeightCgpa());
            pstmt.setInt(4, settings.getWeightAttendance());
            pstmt.setInt(5, settings.getWeightDistance());
            pstmt.setInt(6, settings.getWeightFinancial());
            pstmt.setInt(7, settings.getWeightYear());
            pstmt.executeUpdate();
        }
    }
}
