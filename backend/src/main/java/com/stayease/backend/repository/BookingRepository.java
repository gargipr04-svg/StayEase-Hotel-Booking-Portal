package com.stayease.backend.repository;

import com.stayease.backend.entity.Booking;
import com.stayease.backend.entity.BookingStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    // Check whether a booking reference already exists
    boolean existsByBookingReference(String bookingReference);

    // Find a booking using its reference and guest email
    Optional<Booking> findByBookingReferenceAndEmailIgnoreCase(
            String bookingReference,
            String email
    );

    // Count confirmed bookings that overlap with requested dates
    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.hotel.hotelId = :hotelId
        AND b.status = :status
        AND b.checkIn < :checkOut
        AND b.checkOut > :checkIn
        """)
    long countOverlappingBookings(
            @Param("hotelId") Long hotelId,
            @Param("status") BookingStatus status,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    // Lock a booking while processing cancellation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.bookingReference = :reference
        AND LOWER(b.email) = LOWER(:email)
        """)
    Optional<Booking> findByReferenceAndEmailForUpdate(
            @Param("reference") String reference,
            @Param("email") String email
    );

}
