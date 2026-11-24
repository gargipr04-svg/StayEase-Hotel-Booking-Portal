package com.stayease.backend.repository;

import com.stayease.backend.entity.RoomInventory;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomInventoryRepository
        extends JpaRepository<RoomInventory, Long> {

    // Find room inventory for a particular hotel
    Optional<RoomInventory> findByHotel_HotelId(Long hotelId);

    // Lock room inventory while processing a booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ri FROM RoomInventory ri WHERE ri.hotel.hotelId = :hotelId")
    Optional<RoomInventory> findByHotelIdForUpdate(
            @Param("hotelId") Long hotelId
    );
}
