package com.stayease.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class BookingRequest {

    // ID of the hotel being booked
    @NotNull(message = "Hotel ID is required")
    @Positive(message = "Hotel ID must be positive")
    private Long hotelId;

    // Guest information
    @NotBlank(message = "Guest name is required")
    @Size(max = 150, message = "Guest name cannot exceed 150 characters")
    private String guestName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    // Booking dates
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;

    // Getters and setters

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
