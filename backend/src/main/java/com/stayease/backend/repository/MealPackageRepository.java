package com.stayease.backend.repository;

import com.stayease.backend.entity.MealPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealPackageRepository
        extends JpaRepository<MealPackage, Long> {

    // Find all meal packages offered by a particular hotel
    List<MealPackage> findByHotel_HotelId(Long hotelId);

    // Find available meal packages offered by a particular hotel
    List<MealPackage> findByHotel_HotelIdAndAvailableTrue(Long hotelId);
}
