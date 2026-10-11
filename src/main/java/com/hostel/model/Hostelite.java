package com.hostel.model;

public class Hostelite extends Person {
    private String studentRollNo;
    private Room roomAllocated;
    private String messPlan;
    private double feesDue;
    
    private double cgpa;
    private double attendance;
    private int yearOfStudy;
    private int distanceFromCampus;
    private String medicalNeeds;
    private boolean financialNeed;
    private String conductStatus;
    private String preferredRoomType;

    public Hostelite(int id, String name, String email, String phone, String studentRollNo) {
        super(id, name, email, phone);
        this.studentRollNo = studentRollNo;
    }

    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }

    public Room getRoomAllocated() { return roomAllocated; }
    public void setRoomAllocated(Room roomAllocated) { this.roomAllocated = roomAllocated; }

    public String getMessPlan() { return messPlan; }
    public void setMessPlan(String messPlan) { this.messPlan = messPlan; }

    public double getFeesDue() { return feesDue; }
    public void setFeesDue(double feesDue) { this.feesDue = feesDue; }
    
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public double getAttendance() { return attendance; }
    public void setAttendance(double attendance) { this.attendance = attendance; }

    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public int getDistanceFromCampus() { return distanceFromCampus; }
    public void setDistanceFromCampus(int distanceFromCampus) { this.distanceFromCampus = distanceFromCampus; }

    public String getMedicalNeeds() { return medicalNeeds; }
    public void setMedicalNeeds(String medicalNeeds) { this.medicalNeeds = medicalNeeds; }

    public boolean isFinancialNeed() { return financialNeed; }
    public void setFinancialNeed(boolean financialNeed) { this.financialNeed = financialNeed; }

    public String getConductStatus() { return conductStatus; }
    public void setConductStatus(String conductStatus) { this.conductStatus = conductStatus; }

    public String getPreferredRoomType() { return preferredRoomType; }
    public void setPreferredRoomType(String preferredRoomType) { this.preferredRoomType = preferredRoomType; }
}
