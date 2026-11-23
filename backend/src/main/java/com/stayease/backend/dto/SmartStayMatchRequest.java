package com.stayease.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SmartStayMatchRequest {

    // Customer's maximum budget per night
    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.01", message = "Budget must be greater than zero")
    private BigDecimal budget;

    // Customer's preferred location
    @NotBlank(message = "Preferred location is required")
    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    // Customer's preferred amenities
    @Size(max = 20, message = "You can select up to 20 amenities")
    private List<String> amenities;

    // Required dates for availability checking
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    // Getters and setters

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
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
