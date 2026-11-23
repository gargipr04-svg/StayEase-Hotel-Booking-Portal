package com.stayease.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class HotelResponse {

    private Long hotelId;
    private String name;
    private String location;
    private String description;
    private String imageUrl;

    private String roomType;
    private Integer totalRooms;
    private BigDecimal pricePerNight;

    private List<String> amenities;

    // Constructor
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

        this.hotelId = hotelId;
        this.name = name;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
        this.roomType = roomType;
        this.totalRooms = totalRooms;
        this.pricePerNight = pricePerNight;
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

    public String getRoomType() {
        return roomType;
    }

    public Integer getTotalRooms() {
        return totalRooms;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public List<String> getAmenities() {
        return amenities;
    }
}
