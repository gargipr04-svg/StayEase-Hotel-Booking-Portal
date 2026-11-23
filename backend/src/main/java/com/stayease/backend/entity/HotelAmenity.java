package com.stayease.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "hotel_amenities",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_hotel_amenity",
                        columnNames = {"hotel_id", "amenity_name"}
                )
        }
)
public class HotelAmenity {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amenity_id")
    private Long amenityId;

    // Relationship with Hotel
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // Amenity name
    @Column(name = "amenity_name", nullable = false, length = 100)
    private String amenityName;

    // Default constructor required by JPA
    public HotelAmenity() {
    }

    // Getters and setters

    public Long getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(Long amenityId) {
        this.amenityId = amenityId;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public String getAmenityName() {
        return amenityName;
    }

    public void setAmenityName(String amenityName) {
        this.amenityName = amenityName;
    }
}
