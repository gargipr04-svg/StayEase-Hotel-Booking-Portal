package com.stayease.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class SmartStayMatchResponse {

    private HotelResponse hotel;

    private BigDecimal matchScore;
    private BigDecimal budgetScore;
    private BigDecimal locationScore;
    private BigDecimal amenitiesScore;

    private Integer availableRooms;

    private List<String> reasons;

    // Constructor
    public SmartStayMatchResponse(
            HotelResponse hotel,
            BigDecimal matchScore,
            BigDecimal budgetScore,
            BigDecimal locationScore,
            BigDecimal amenitiesScore,
            Integer availableRooms,
            List<String> reasons) {

        this.hotel = hotel;
        this.matchScore = matchScore;
        this.budgetScore = budgetScore;
        this.locationScore = locationScore;
        this.amenitiesScore = amenitiesScore;
        this.availableRooms = availableRooms;
        this.reasons = reasons;
    }

    // Getters

    public HotelResponse getHotel() {
        return hotel;
    }

    public BigDecimal getMatchScore() {
        return matchScore;
    }

    public BigDecimal getBudgetScore() {
        return budgetScore;
    }

    public BigDecimal getLocationScore() {
        return locationScore;
    }

    public BigDecimal getAmenitiesScore() {
        return amenitiesScore;
    }

    public Integer getAvailableRooms() {
        return availableRooms;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
