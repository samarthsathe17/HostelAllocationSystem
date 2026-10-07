package com.hostel.model;

public class SingleOccupancy extends Room {
    public SingleOccupancy(String roomNumber, int floor, double basePrice, int currentOccupancy) {
        super(roomNumber, floor, basePrice, 1, currentOccupancy);
    }
}
