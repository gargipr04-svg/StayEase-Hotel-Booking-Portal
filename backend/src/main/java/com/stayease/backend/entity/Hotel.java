package com.stayease.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotels")
public class Hotel {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;

    // Hotel name
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // Hotel location
    @Column(name = "location", nullable = false, length = 150)
    private String location;

    // Hotel description
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Hotel image URL
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // Hotel highlights
    @Column(name = "highlights", columnDefinition = "TEXT")
    private String highlights;

    // Nearby tourist attractions
    @Column(name = "nearby_attractions", columnDefinition = "TEXT")
    private String nearbyAttractions;

    // Type of guests the hotel is ideal for
    @Column(name = "ideal_for", length = 500)
    private String idealFor;

    // Record creation time (managed by MySQL)
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public Hotel() {
    }

    // Getters and setters

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHighlights() {
        return highlights;
    }

    public void setHighlights(String highlights) {
        this.highlights = highlights;
    }

    public String getNearbyAttractions() {
        return nearbyAttractions;
    }

    public void setNearbyAttractions(String nearbyAttractions) {
        this.nearbyAttractions = nearbyAttractions;
    }

    public String getIdealFor() {
        return idealFor;
    }

    public void setIdealFor(String idealFor) {
        this.idealFor = idealFor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
