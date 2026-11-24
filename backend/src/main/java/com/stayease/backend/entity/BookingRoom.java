package com.stayease.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "booking_rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"booking_id", "room_number"}
                )
        }
)
public class BookingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_room_id")
    private Long bookingRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @Column(name = "adults_count", nullable = false)
    private Integer adultsCount;

    @Column(name = "children_5_to_11_count", nullable = false)
    private Integer children5To11Count;

    @Column(name = "children_under_5_count", nullable = false)
    private Integer childrenUnder5Count;

    public BookingRoom() {
    }

    public Long getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(Long bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
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
