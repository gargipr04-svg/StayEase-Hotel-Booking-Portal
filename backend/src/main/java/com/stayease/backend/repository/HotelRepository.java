package com.stayease.backend.repository;

import com.stayease.backend.entity.Hotel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    // Search hotels using their location
    List<Hotel> findByLocationContainingIgnoreCase(String location);

}
