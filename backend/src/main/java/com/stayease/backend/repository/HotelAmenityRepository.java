package com.stayease.backend.repository;

import com.stayease.backend.entity.HotelAmenity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelAmenityRepository
        extends JpaRepository<HotelAmenity, Long> {

    // Retrieve all amenities belonging to a particular hotel
    List<HotelAmenity> findByHotel_HotelId(Long hotelId);

}
