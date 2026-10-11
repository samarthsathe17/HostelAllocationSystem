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
import com.hostel.dao.SettingsDAO;
import com.hostel.dao.AllocationRequestDAO;
import com.hostel.model.AllocationSettings;
import com.hostel.model.AllocationRequest;
import com.hostel.service.AllocationService;

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
        
        server.createContext("/api/settings", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                try {
                    SettingsDAO settingsDAO = new SettingsDAO();
                    if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                        AllocationSettings settings = settingsDAO.getSettings();
                        String response = "{" +
                                "\"minCgpa\":" + settings.getMinCgpa() + "," +
                                "\"minAttendance\":" + settings.getMinAttendance() + "," +
                                "\"weightCgpa\":" + settings.getWeightCgpa() + "," +
                                "\"weightAttendance\":" + settings.getWeightAttendance() + "," +
                                "\"weightDistance\":" + settings.getWeightDistance() + "," +
                                "\"weightFinancial\":" + settings.getWeightFinancial() + "," +
                                "\"weightYear\":" + settings.getWeightYear() + "}";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        InputStream is = exchange.getRequestBody();
                        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        
                        AllocationSettings settings = new AllocationSettings();
                        settings.setMinCgpa(Double.parseDouble(extractJsonValue(body, "minCgpa")));
                        settings.setMinAttendance(Double.parseDouble(extractJsonValue(body, "minAttendance")));
                        settings.setWeightCgpa(Integer.parseInt(extractJsonValue(body, "weightCgpa")));
                        settings.setWeightAttendance(Integer.parseInt(extractJsonValue(body, "weightAttendance")));
                        settings.setWeightDistance(Integer.parseInt(extractJsonValue(body, "weightDistance")));
                        settings.setWeightFinancial(Integer.parseInt(extractJsonValue(body, "weightFinancial")));
                        settings.setWeightYear(Integer.parseInt(extractJsonValue(body, "weightYear")));
                        
                        settingsDAO.updateSettings(settings);
                        
                        String response = "{\"status\":\"success\"}";
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
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

        server.createContext("/api/allocate/requests", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                try {
                    AllocationRequestDAO requestDAO = new AllocationRequestDAO();
                    com.hostel.dao.StudentDAO studentDAO = new com.hostel.dao.StudentDAO();
                    List<AllocationRequest> requests = requestDAO.getAllRequests();
                    
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < requests.size(); i++) {
                        AllocationRequest req = requests.get(i);
                        Hostelite s = studentDAO.getAllStudents().stream().filter(st -> st.getId() == req.getStudentId()).findFirst().orElse(null);
                        String sName = s != null ? s.getName() : "Unknown";
                        String sRoll = s != null ? s.getStudentRollNo() : "Unknown";
                        
                        json.append("{")
                            .append("\"requestId\":").append(req.getRequestId()).append(",")
                            .append("\"studentName\":\"").append(sName).append("\",")
                            .append("\"studentRollNo\":\"").append(sRoll).append("\",")
                            .append("\"status\":\"").append(req.getStatus()).append("\",")
                            .append("\"priorityScore\":").append(req.getPriorityScore()).append(",")
                            .append("\"isEligible\":").append(req.isEligible()).append(",")
                            .append("\"eligibilityDetails\":\"").append(req.getEligibilityDetails().replace("\"", "\\\"")).append("\",")
                            .append("\"recommendedRoom\":").append(req.getRecommendedRoom() == null ? "null" : "\"" + req.getRecommendedRoom() + "\"")
                            .append("}");
                        if (i < requests.size() - 1) json.append(",");
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

        server.createContext("/api/allocate/confirm", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                addCorsHeaders(exchange);
                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try {
                        InputStream is = exchange.getRequestBody();
                        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        int requestId = Integer.parseInt(extractJsonValue(body, "requestId"));
                        String action = extractJsonValue(body, "action"); // APPROVE or REJECT
                        String roomNo = extractJsonValue(body, "roomNo");
                        
                        AllocationRequestDAO requestDAO = new AllocationRequestDAO();
                        AllocationRequest req = requestDAO.getRequestById(requestId);
                        
                        if ("APPROVE".equals(action)) {
                            AllocationDAO allocationDAO = new AllocationDAO();
                            allocationDAO.allocateRoom(req.getStudentId(), roomNo, "STANDARD"); // default mess plan
                            requestDAO.updateRequestStatus(requestId, "APPROVED");
                        } else {
                            requestDAO.updateRequestStatus(requestId, "REJECTED");
                        }
                        
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
                    
                    String name = extractJsonValue(body, "name");
                    String rollNo = extractJsonValue(body, "rollNo");
                    String email = extractJsonValue(body, "email");
                    String phone = extractJsonValue(body, "phone");
                    String preferredRoomType = extractJsonValue(body, "preferredRoomType");
                    
                    double cgpa = 0.0;
                    double attendance = 0.0;
                    int yearOfStudy = 1;
                    int distanceFromCampus = 0;
                    boolean financialNeed = false;
                    String medicalNeeds = "";
                    String conductStatus = "GOOD";
                    
                    try {
                        cgpa = Double.parseDouble(extractJsonValue(body, "cgpa"));
                        attendance = Double.parseDouble(extractJsonValue(body, "attendance"));
                        yearOfStudy = Integer.parseInt(extractJsonValue(body, "yearOfStudy"));
                        distanceFromCampus = Integer.parseInt(extractJsonValue(body, "distanceFromCampus"));
                        financialNeed = Boolean.parseBoolean(extractJsonValue(body, "financialNeed"));
                        medicalNeeds = extractJsonValue(body, "medicalNeeds");
                        conductStatus = extractJsonValue(body, "conductStatus");
                        if (conductStatus.isEmpty()) conductStatus = "GOOD";
                        if (preferredRoomType.isEmpty()) preferredRoomType = "SHARED";
                    } catch (Exception ignored) { }
                    
                    try {
                        com.hostel.dao.StudentDAO studentDAO = new com.hostel.dao.StudentDAO();
                        Hostelite student = studentDAO.getStudentByRollNo(rollNo);
                        if (student == null) {
                            student = new Hostelite(0, name, email, phone, rollNo);
                            student.setCgpa(cgpa);
                            student.setAttendance(attendance);
                            student.setYearOfStudy(yearOfStudy);
                            student.setDistanceFromCampus(distanceFromCampus);
                            student.setMedicalNeeds(medicalNeeds);
                            student.setFinancialNeed(financialNeed);
                            student.setConductStatus(conductStatus);
                            student.setPreferredRoomType(preferredRoomType);
                            studentDAO.addStudent(student);
                        } else {
                            // Ideally update the existing student but for simplicity, we just use the existing one, 
                            // maybe updating properties here would be better.
                            // Let's assume the existing student is already correct or update is not supported here.
                        }
                        
                        AllocationService service = new AllocationService();
                        AllocationRequest req = service.processStudentApplication(student);
                        
                        String response = "{\"status\":\"success\", \"message\":\"Application submitted successfully. Priority Score: " + req.getPriorityScore() + "\"}";
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
