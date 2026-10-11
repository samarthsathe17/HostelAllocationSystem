package com.hostel.model;

public class AllocationSettings {
    private int id;
    private double minCgpa;
    private double minAttendance;
    private int weightCgpa;
    private int weightAttendance;
    private int weightDistance;
    private int weightFinancial;
    private int weightYear;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }
    
    public double getMinAttendance() { return minAttendance; }
    public void setMinAttendance(double minAttendance) { this.minAttendance = minAttendance; }
    
    public int getWeightCgpa() { return weightCgpa; }
    public void setWeightCgpa(int weightCgpa) { this.weightCgpa = weightCgpa; }
    
    public int getWeightAttendance() { return weightAttendance; }
    public void setWeightAttendance(int weightAttendance) { this.weightAttendance = weightAttendance; }
    
    public int getWeightDistance() { return weightDistance; }
    public void setWeightDistance(int weightDistance) { this.weightDistance = weightDistance; }
    
    public int getWeightFinancial() { return weightFinancial; }
    public void setWeightFinancial(int weightFinancial) { this.weightFinancial = weightFinancial; }
    
    public int getWeightYear() { return weightYear; }
    public void setWeightYear(int weightYear) { this.weightYear = weightYear; }
}
