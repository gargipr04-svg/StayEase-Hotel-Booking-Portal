package com.stayease.backend.repository;

import com.stayease.backend.entity.Hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Search hotels using their location
    List<Hotel> findByLocationContainingIgnoreCase(String location);

    // Retrieve all unique hotel locations
    @Query("""
        SELECT DISTINCT h.location
        FROM Hotel h
        ORDER BY h.location ASC
        """)
    List<String> findAllDistinctLocations();

}
