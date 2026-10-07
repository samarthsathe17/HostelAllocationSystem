package com.hostel.model;

public abstract class Room {
    private String roomNumber;
    private int floor;
    private double basePrice;
    private int capacity;
    private int currentOccupancy;

    public Room(String roomNumber, int floor, double basePrice, int capacity, int currentOccupancy) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
    }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public boolean isAvailable() {
        return currentOccupancy < capacity;
    }
}
