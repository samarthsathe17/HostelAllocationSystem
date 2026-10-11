package com.hostel.service;

import com.hostel.dao.AllocationRequestDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.SettingsDAO;
import com.hostel.model.AllocationRequest;
import com.hostel.model.AllocationSettings;
import com.hostel.model.Hostelite;
import com.hostel.model.Room;

import java.sql.SQLException;
import java.util.List;

public class AllocationService {
    
    private SettingsDAO settingsDAO = new SettingsDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private AllocationRequestDAO requestDAO = new AllocationRequestDAO();

    public AllocationRequest processStudentApplication(Hostelite student) throws SQLException {
        AllocationSettings settings = settingsDAO.getSettings();
        if (settings == null) {
            throw new RuntimeException("Settings not configured");
        }

        AllocationRequest request = new AllocationRequest();
        request.setStudentId(student.getId());
        request.setStatus("PENDING");

        // 1. Check Eligibility (Hard Requirements)
        StringBuilder eligibilityDetails = new StringBuilder();
        boolean isEligible = true;

        if (student.getCgpa() < settings.getMinCgpa()) {
            isEligible = false;
            eligibilityDetails.append(String.format("CGPA (%.2f) is below minimum (%.2f). ", student.getCgpa(), settings.getMinCgpa()));
        } else {
            eligibilityDetails.append(String.format("CGPA (%.2f) met. ", student.getCgpa()));
        }

        if (student.getAttendance() < settings.getMinAttendance()) {
            isEligible = false;
            eligibilityDetails.append(String.format("Attendance (%.2f%%) is below minimum (%.2f%%). ", student.getAttendance(), settings.getMinAttendance()));
        } else {
            eligibilityDetails.append(String.format("Attendance (%.2f%%) met. ", student.getAttendance()));
        }

        if ("SUSPENDED".equals(student.getConductStatus())) {
            isEligible = false;
            eligibilityDetails.append("Student is currently suspended. ");
        }

        request.setEligible(isEligible);
        request.setEligibilityDetails(eligibilityDetails.toString().trim());

        // 2. Calculate Priority Score
        double score = 0.0;
        
        // Normalize out of 10 and apply weight
        double cgpaScore = (student.getCgpa() / 10.0) * settings.getWeightCgpa();
        double attendanceScore = (student.getAttendance() / 100.0) * settings.getWeightAttendance();
        
        // Distance score: max at 1000km
        double distanceNorm = Math.min(student.getDistanceFromCampus() / 1000.0, 1.0);
        double distanceScore = distanceNorm * settings.getWeightDistance();
        
        // Financial Need score
        double financialScore = student.isFinancialNeed() ? settings.getWeightFinancial() : 0.0;
        
        // Year of study: prioritize later years
        double yearNorm = Math.min(student.getYearOfStudy() / 4.0, 1.0);
        double yearScore = yearNorm * settings.getWeightYear();
        
        score = cgpaScore + attendanceScore + distanceScore + financialScore + yearScore;
        request.setPriorityScore(score);

        // 3. Recommend Room (if eligible)
        if (isEligible) {
            String recommendedRoom = findBestRoomForStudent(student);
            request.setRecommendedRoom(recommendedRoom);
            if (recommendedRoom == null) {
                request.setEligibilityDetails(request.getEligibilityDetails() + " [No suitable room available]");
            }
        }

        // Save request
        requestDAO.addRequest(request);
        return request;
    }

    private String findBestRoomForStudent(Hostelite student) throws SQLException {
        List<Room> allRooms = roomDAO.getAllRooms();
        for (Room room : allRooms) {
            if (room.getCurrentOccupancy() < room.getCapacity()) {
                String roomType = room instanceof com.hostel.model.SingleOccupancy ? "SINGLE" : "SHARED";
                if (roomType.equals(student.getPreferredRoomType())) {
                    return room.getRoomNumber();
                }
            }
        }
        // Fallback: any available room
        for (Room room : allRooms) {
            if (room.getCurrentOccupancy() < room.getCapacity()) {
                return room.getRoomNumber();
            }
        }
        return null; // Room Full
    }
}
