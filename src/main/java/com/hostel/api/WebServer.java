package com.hostel.api;

import com.hostel.dao.RoomDAO;
import com.hostel.dao.AllocationDAO;
import com.hostel.model.Hostelite;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class WebServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/api/rooms/available", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                RoomDAO dao = new RoomDAO();
                List<String> rooms = dao.getAvailableRoomNumbers();
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < rooms.size(); i++) {
                    json.append("\"").append(rooms.get(i)).append("\"");
                    if (i < rooms.size() - 1) json.append(",");
                }
                json.append("]");
                String response = json.toString();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.createContext("/api/rooms", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                
                try {
                    RoomDAO dao = new RoomDAO();
                    List<com.hostel.model.Room> rooms = dao.getAllRooms();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < rooms.size(); i++) {
                        com.hostel.model.Room r = rooms.get(i);
                        String type = (r instanceof com.hostel.model.SingleOccupancy) ? "SINGLE" : "SHARED";
                        json.append("{")
                            .append("\"room_no\":\"").append(r.getRoomNumber()).append("\",")
                            .append("\"type\":\"").append(type).append("\",")
                            .append("\"capacity\":").append(r.getCapacity()).append(",")
                            .append("\"current_occupancy\":").append(r.getCurrentOccupancy()).append(",")
                            .append("\"base_price\":").append(r.getBasePrice())
                            .append("}");
                        if (i < rooms.size() - 1) json.append(",");
                    }
                    json.append("]");
                    String response = json.toString();
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    String response = "{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        });
        
        server.createContext("/api/allocate", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    InputStream is = exchange.getRequestBody();
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    
                    // Simple JSON parse: {"name":"John", "rollNo":"123", "email":"e@e.com", "phone":"123", "roomNo":"101", "messPlan":"STANDARD"}
                    String name = extractJsonValue(body, "name");
                    String rollNo = extractJsonValue(body, "rollNo");
                    String email = extractJsonValue(body, "email");
                    String phone = extractJsonValue(body, "phone");
                    String roomNo = extractJsonValue(body, "roomNo");
                    String messPlan = extractJsonValue(body, "messPlan");
                    
                    try {
                        com.hostel.dao.StudentDAO studentDAO = new com.hostel.dao.StudentDAO();
                        Hostelite student = studentDAO.getStudentByRollNo(rollNo);
                        if (student == null) {
                            student = new Hostelite(0, name, email, phone, rollNo);
                            studentDAO.addStudent(student);
                        }
                        
                        AllocationDAO allocationDAO = new AllocationDAO();
                        allocationDAO.allocateRoom(student.getId(), roomNo, messPlan);
                        String response = "{\"status\":\"success\"}";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        String response = "{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(500, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                }
            }
        });

        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("API Server started on port 8080");
    }
    
    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
    
    private static String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":";
        int index = json.indexOf(search);
        if (index == -1) return "";
        index += search.length();
        int startQuote = json.indexOf("\"", index);
        if (startQuote == -1) return "";
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return "";
        return json.substring(startQuote + 1, endQuote);
    }
}
