package com.hostel.model;

public class Warden extends Person {
    private String employeeId;
    private String department;

    public Warden(int id, String name, String email, String phone, String employeeId, String department) {
        super(id, name, email, phone);
        this.employeeId = employeeId;
        this.department = department;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
