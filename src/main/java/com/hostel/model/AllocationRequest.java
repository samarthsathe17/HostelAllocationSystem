package com.hostel.model;

import java.sql.Timestamp;

public class AllocationRequest {
    private int requestId;
    private int studentId;
    private Timestamp requestDate;
    private String status;
    private double priorityScore;
    private boolean isEligible;
    private String eligibilityDetails;
    private String recommendedRoom;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }

    public boolean isEligible() { return isEligible; }
    public void setEligible(boolean eligible) { isEligible = eligible; }

    public String getEligibilityDetails() { return eligibilityDetails; }
    public void setEligibilityDetails(String eligibilityDetails) { this.eligibilityDetails = eligibilityDetails; }

    public String getRecommendedRoom() { return recommendedRoom; }
    public void setRecommendedRoom(String recommendedRoom) { this.recommendedRoom = recommendedRoom; }
}
