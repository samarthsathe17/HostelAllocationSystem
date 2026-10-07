package com.hostel.dao;

import com.hostel.model.Room;
import com.hostel.model.SharedOccupancy;
import com.hostel.model.SingleOccupancy;
import com.hostel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_no";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        }
        return rooms;
    }

    public List<String> getAvailableRoomNumbers() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT room_no, type, capacity, current_occupancy FROM rooms WHERE current_occupancy < capacity";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String roomNo = rs.getString("room_no");
                String type = rs.getString("type");
                list.add("Room " + roomNo + " (" + type + ")");
            }
            System.out.println("Available rooms fetched: " + list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Room getRoom(String roomNo) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        }
        return null;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        String roomNo = rs.getString("room_no");
        String type = rs.getString("type");
        int capacity = rs.getInt("capacity");
        int currentOccupancy = rs.getInt("current_occupancy");
        double basePrice = rs.getDouble("base_price");
        
        int floor = Integer.parseInt(roomNo.substring(0, 1)); // Assuming first digit is floor

        if ("SINGLE".equals(type)) {
            return new SingleOccupancy(roomNo, floor, basePrice, currentOccupancy);
        } else {
            return new SharedOccupancy(roomNo, floor, basePrice, capacity, currentOccupancy);
        }
    }
}
