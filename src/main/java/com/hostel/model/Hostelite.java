package com.hostel.model;

public class Hostelite extends Person {
    private String studentRollNo;
    private Room roomAllocated;
    private String messPlan;
    private double feesDue;

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
}
