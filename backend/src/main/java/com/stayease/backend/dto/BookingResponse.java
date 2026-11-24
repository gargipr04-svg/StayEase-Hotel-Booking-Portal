package com.stayease.backend.dto;

import com.stayease.backend.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    private Integer roomsCount;
    private Integer adultsCount;
    private Integer children5To11Count;
    private Integer childrenUnder5Count;

    private String mealPackageName;
    private BigDecimal mealPricePerAdultPerNight;

    private BigDecimal roomTotal;
    private BigDecimal mealTotal;

    private List<RoomResponse> rooms;

    private BookingStatus status;

    // Original constructor retained for compatibility
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

        this.roomsCount = 1;
        this.adultsCount = 1;
        this.children5To11Count = 0;
        this.childrenUnder5Count = 0;

        this.roomTotal = totalAmount;
        this.mealTotal = BigDecimal.ZERO;
        this.mealPricePerAdultPerNight = BigDecimal.ZERO;
        this.rooms = List.of();
    }

    // Extended constructor for the new booking system
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
            Integer roomsCount,
            Integer adultsCount,
            Integer children5To11Count,
            Integer childrenUnder5Count,
            String mealPackageName,
            BigDecimal mealPricePerAdultPerNight,
            BigDecimal roomTotal,
            BigDecimal mealTotal,
            List<RoomResponse> rooms,
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
        this.roomsCount = roomsCount;
        this.adultsCount = adultsCount;
        this.children5To11Count = children5To11Count;
        this.childrenUnder5Count = childrenUnder5Count;
        this.mealPackageName = mealPackageName;
        this.mealPricePerAdultPerNight = mealPricePerAdultPerNight;
        this.roomTotal = roomTotal;
        this.mealTotal = mealTotal;
        this.rooms = rooms == null ? List.of() : List.copyOf(rooms);
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

    public Integer getRoomsCount() {
        return roomsCount;
    }

    public Integer getAdultsCount() {
        return adultsCount;
    }

    public Integer getChildren5To11Count() {
        return children5To11Count;
    }

    public Integer getChildrenUnder5Count() {
        return childrenUnder5Count;
    }

    public String getMealPackageName() {
        return mealPackageName;
    }

    public BigDecimal getMealPricePerAdultPerNight() {
        return mealPricePerAdultPerNight;
    }

    public BigDecimal getRoomTotal() {
        return roomTotal;
    }

    public BigDecimal getMealTotal() {
        return mealTotal;
    }

    public List<RoomResponse> getRooms() {
        return rooms;
    }

    public BookingStatus getStatus() {
        return status;
    }

    // Individual room information in the response
    public static class RoomResponse {

        private Integer roomNumber;
        private Integer adultsCount;
        private Integer children5To11Count;
        private Integer childrenUnder5Count;

        public RoomResponse(
                Integer roomNumber,
                Integer adultsCount,
                Integer children5To11Count,
                Integer childrenUnder5Count) {

            this.roomNumber = roomNumber;
            this.adultsCount = adultsCount;
            this.children5To11Count = children5To11Count;
            this.childrenUnder5Count = childrenUnder5Count;
        }

        public Integer getRoomNumber() {
            return roomNumber;
        }

        public Integer getAdultsCount() {
            return adultsCount;
        }

        public Integer getChildren5To11Count() {
            return children5To11Count;
        }

        public Integer getChildrenUnder5Count() {
            return childrenUnder5Count;
        }
    }
}
