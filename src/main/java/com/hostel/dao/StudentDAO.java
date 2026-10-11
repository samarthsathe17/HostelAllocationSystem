package com.hostel.dao;

import com.hostel.model.Hostelite;
import com.hostel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Hostelite student) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, email, phone, cgpa, attendance, year_of_study, distance_from_campus, medical_needs, financial_need, conduct_status, preferred_room_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getStudentRollNo());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setDouble(5, student.getCgpa());
            pstmt.setDouble(6, student.getAttendance());
            pstmt.setInt(7, student.getYearOfStudy());
            pstmt.setInt(8, student.getDistanceFromCampus());
            pstmt.setString(9, student.getMedicalNeeds());
            pstmt.setBoolean(10, student.isFinancialNeed());
            pstmt.setString(11, student.getConductStatus() != null ? student.getConductStatus() : "GOOD");
            pstmt.setString(12, student.getPreferredRoomType() != null ? student.getPreferredRoomType() : "SHARED");
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Hostelite getStudentByRollNo(String rollNo) throws SQLException {
        String sql = "SELECT * FROM students WHERE roll_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rollNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Hostelite h = new Hostelite(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("roll_no")
                    );
                    h.setCgpa(rs.getDouble("cgpa"));
                    h.setAttendance(rs.getDouble("attendance"));
                    h.setYearOfStudy(rs.getInt("year_of_study"));
                    h.setDistanceFromCampus(rs.getInt("distance_from_campus"));
                    h.setMedicalNeeds(rs.getString("medical_needs"));
                    h.setFinancialNeed(rs.getBoolean("financial_need"));
                    h.setConductStatus(rs.getString("conduct_status"));
                    h.setPreferredRoomType(rs.getString("preferred_room_type"));
                    return h;
                }
            }
        }
        return null;
    }
    
    public List<Hostelite> getAllStudents() throws SQLException {
        List<Hostelite> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Hostelite h = new Hostelite(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("roll_no")
                );
                h.setCgpa(rs.getDouble("cgpa"));
                h.setAttendance(rs.getDouble("attendance"));
                h.setYearOfStudy(rs.getInt("year_of_study"));
                h.setDistanceFromCampus(rs.getInt("distance_from_campus"));
                h.setMedicalNeeds(rs.getString("medical_needs"));
                h.setFinancialNeed(rs.getBoolean("financial_need"));
                h.setConductStatus(rs.getString("conduct_status"));
                h.setPreferredRoomType(rs.getString("preferred_room_type"));
                students.add(h);
            }
        }
        return students;
    }
}
