package com.stayease.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class HotelResponse {

    private Long hotelId;
    private String name;
    private String location;
    private String description;
    private String imageUrl;

    private String highlights;
    private String nearbyAttractions;
    private String idealFor;

    private String roomType;
    private Integer totalRooms;
    private BigDecimal pricePerNight;

    private Integer maxAdultsPerRoom;
    private Integer maxChildrenPerRoom;
    private Integer maxOccupancy;

    private List<String> amenities;

    // Original constructor - retained for compatibility
    public HotelResponse(
            Long hotelId,
            String name,
            String location,
            String description,
            String imageUrl,
            String roomType,
            Integer totalRooms,
            BigDecimal pricePerNight,
            List<String> amenities) {

        this(
                hotelId,
                name,
                location,
                description,
                imageUrl,
                null,
                null,
                null,
                roomType,
                totalRooms,
                pricePerNight,
                null,
                null,
                null,
                amenities
        );
    }

    // Extended constructor
    public HotelResponse(
            Long hotelId,
            String name,
            String location,
            String description,
            String imageUrl,
            String highlights,
            String nearbyAttractions,
            String idealFor,
            String roomType,
            Integer totalRooms,
            BigDecimal pricePerNight,
            Integer maxAdultsPerRoom,
            Integer maxChildrenPerRoom,
            Integer maxOccupancy,
            List<String> amenities) {

        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;

        this.highlights = highlights;
        this.nearbyAttractions = nearbyAttractions;
        this.idealFor = idealFor;

        this.roomType = roomType;
        this.totalRooms = totalRooms;
        this.pricePerNight = pricePerNight;

        this.maxAdultsPerRoom = maxAdultsPerRoom;
        this.maxChildrenPerRoom = maxChildrenPerRoom;
        this.maxOccupancy = maxOccupancy;

        this.amenities = amenities;
    }

    // Getters

    public Long getHotelId() {
        return hotelId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHighlights() {
        return highlights;
    }

    public String getNearbyAttractions() {
        return nearbyAttractions;
    }

    public String getIdealFor() {
        return idealFor;
    }

    public String getRoomType() {
        return roomType;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public Integer getMaxAdultsPerRoom() {
        return maxAdultsPerRoom;
    }

    public Integer getMaxChildrenPerRoom() {
        return maxChildrenPerRoom;
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public List<String> getAmenities() {
        return amenities;
    }
}
