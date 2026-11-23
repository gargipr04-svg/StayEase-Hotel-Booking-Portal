
package com.stayease.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import java.math.BigDecimal;

@Entity
@Table(name = "room_inventory")
public class RoomInventory {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    // Relationship with Hotel
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false, unique = true)
    private Hotel hotel;

    // Room type
    @Column(name = "room_type", nullable = false, length = 100)
    private String roomType;

    // Total available rooms in the hotel
    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;

    // Price per night
    @Column(name = "price_per_night", nullable = false,
            precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    // Default constructor required by JPA
    public RoomInventory() {
    }

    // Getters and setters

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}
