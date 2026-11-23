package com.stayease.backend.dto;

import com.stayease.backend.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingResponse {

    private String bookingReference;

    private Long hotelId;
    private String hotelName;
    private String location;

    private String guestName;
    private String email;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer nights;

    private BigDecimal pricePerNight;
    private BigDecimal totalAmount;

    private BookingStatus status;

    // Constructor
    public BookingResponse(
            String bookingReference,
            Long hotelId,
            String hotelName,
            String location,
            String guestName,
            String email,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer nights,
            BigDecimal pricePerNight,
            BigDecimal totalAmount,
            BookingStatus status) {

        this.bookingReference = bookingReference;
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.location = location;
        this.guestName = guestName;
        this.email = email;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters

    public String getBookingReference() {
        return bookingReference;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getLocation() {
        return location;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Integer getNights() {
        return nights;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
