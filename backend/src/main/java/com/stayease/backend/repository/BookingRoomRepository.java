package com.stayease.backend.repository;

import com.stayease.backend.entity.BookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRoomRepository
        extends JpaRepository<BookingRoom, Long> {

    // Find all rooms belonging to a particular booking
    List<BookingRoom> findByBooking_BookingIdOrderByRoomNumberAsc(
            Long bookingId
    );
}
