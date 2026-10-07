package com.hostel.dao;

import com.hostel.model.Hostelite;
import com.hostel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public void addStudent(Hostelite student) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getStudentRollNo());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
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
                    return new Hostelite(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("roll_no")
                    );
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
                students.add(new Hostelite(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("roll_no")
                ));
            }
        }
        return students;
    }
}
