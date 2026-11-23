package com.stayease.backend.controller;

// DTO imports
import com.stayease.backend.dto.BookingRequest;
import com.stayease.backend.dto.BookingResponse;
import com.stayease.backend.dto.BookingAccessRequest;

// Service import
import com.stayease.backend.service.BookingService;

// Spring imports
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    // Constructor injection
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ==========================================
    // 1. CREATE A BOOKING
    // POST /api/bookings
    // ==========================================

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {

        BookingResponse response =
                bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ==========================================
    // 2. RETRIEVE A BOOKING
    // GET /api/bookings/{reference}?email=...
    // ==========================================

    @GetMapping("/{reference}")
    public ResponseEntity<BookingResponse> getBooking(

            @PathVariable String reference,

            @RequestParam String email) {

        BookingResponse response =
                bookingService.getBooking(reference, email);

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // 3. CANCEL A BOOKING
    // PATCH /api/bookings/{reference}/cancel
    // ==========================================

    @PatchMapping("/{reference}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(

            @PathVariable String reference,

            @Valid @RequestBody BookingAccessRequest request) {

        BookingResponse response =
                bookingService.cancelBooking(
                        reference,
                        request.getEmail()
                );

        return ResponseEntity.ok(response);
    }

}
