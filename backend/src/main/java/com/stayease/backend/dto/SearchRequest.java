package com.stayease.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SearchRequest {

    // Customer's preferred location
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    // Desired check-in date
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    // Desired check-out date
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    // Getters and setters

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}
