package com.stayease.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

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

    // Optional meal package selected by the customer
    @Positive(message = "Meal package ID must be positive")
    private Long mealPackageId;

    // Guest allocation for each room
    @NotEmpty(message = "At least one room must be selected")
    @Valid
    private List<RoomAllocation> rooms;

    // Default constructor
    public BookingRequest() {
    }

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

    public Long getMealPackageId() {
        return mealPackageId;
    }

    public void setMealPackageId(Long mealPackageId) {
        this.mealPackageId = mealPackageId;
    }

    public List<RoomAllocation> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomAllocation> rooms) {
        this.rooms = rooms;
    }

    // Individual room guest allocation
    public static class RoomAllocation {

        @NotNull(message = "Room number is required")
        @Positive(message = "Room number must be positive")
        private Integer roomNumber;

        @NotNull(message = "Number of adults is required")
        @Min(value = 1, message = "Every room must have at least one adult")
        private Integer adultsCount;

        @NotNull(message = "Number of children aged 5 to 11 is required")
        @Min(value = 0, message = "Children count cannot be negative")
        private Integer children5To11Count;

        @NotNull(message = "Number of children under 5 is required")
        @Min(value = 0, message = "Children count cannot be negative")
        private Integer childrenUnder5Count;

        public RoomAllocation() {
        }

        public Integer getRoomNumber() {
            return roomNumber;
        }

        public void setRoomNumber(Integer roomNumber) {
            this.roomNumber = roomNumber;
        }

        public Integer getAdultsCount() {
            return adultsCount;
        }

        public void setAdultsCount(Integer adultsCount) {
            this.adultsCount = adultsCount;
        }

        public Integer getChildren5To11Count() {
            return children5To11Count;
        }

        public void setChildren5To11Count(Integer children5To11Count) {
            this.children5To11Count = children5To11Count;
        }

        public Integer getChildrenUnder5Count() {
            return childrenUnder5Count;
        }

        public void setChildrenUnder5Count(Integer childrenUnder5Count) {
            this.childrenUnder5Count = childrenUnder5Count;
        }
    }
}
