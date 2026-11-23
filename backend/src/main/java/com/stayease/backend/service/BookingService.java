package com.stayease.backend.service;

// DTO imports
import com.stayease.backend.dto.BookingRequest;
import com.stayease.backend.dto.BookingResponse;

// Entity imports
import com.stayease.backend.entity.Booking;
import com.stayease.backend.entity.BookingStatus;
import com.stayease.backend.entity.Hotel;
import com.stayease.backend.entity.RoomInventory;

// Repository imports
import com.stayease.backend.repository.BookingRepository;
import com.stayease.backend.repository.RoomInventoryRepository;

// Spring imports
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

// Java imports
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final Clock clock;

    // Constructor injection
    public BookingService(
            BookingRepository bookingRepository,
            RoomInventoryRepository roomInventoryRepository,
            Clock clock) {

        this.bookingRepository = bookingRepository;
        this.roomInventoryRepository = roomInventoryRepository;
        this.clock = clock;
    }

    // ==========================================
    // 1. CREATE A NEW BOOKING
    // ==========================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse createBooking(BookingRequest request) {

        // Validate customer information
        if (request == null ||
                request.getHotelId() == null ||
                request.getHotelId() <= 0) {

            throw new IllegalArgumentException("Valid hotel ID is required.");
        }

        if (request.getGuestName() == null ||
                request.getGuestName().isBlank()) {

            throw new IllegalArgumentException("Guest name is required.");
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new IllegalArgumentException("Email is required.");
        }

        // Validate booking dates
        validateStayDates(request.getCheckIn(), request.getCheckOut());

        /*
         * Lock the hotel's inventory first.
         * This prevents simultaneous bookings from consuming
         * the same last available room.
         */
        RoomInventory inventory = roomInventoryRepository
                .findByHotelIdForUpdate(request.getHotelId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Hotel or room inventory not found."
                        )
                );

        // Check existing confirmed bookings
        long bookedRooms = bookingRepository.countOverlappingBookings(
                request.getHotelId(),
                BookingStatus.CONFIRMED,
                request.getCheckIn(),
                request.getCheckOut()
        );

        // Calculate available rooms
        long availableRooms = inventory.getTotalRooms() - bookedRooms;

        if (availableRooms <= 0) {
            throw new IllegalStateException(
                    "No rooms are available for the selected dates."
            );
        }

        // Calculate number of nights
        long nightsLong = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        int nights = Math.toIntExact(nightsLong);

        // Get the actual price from the database
        BigDecimal pricePerNight = inventory.getPricePerNight();

        // Calculate total booking amount
        BigDecimal totalAmount = pricePerNight.multiply(
                BigDecimal.valueOf(nights)
        );

        // Create booking entity
        Booking booking = new Booking();

        Hotel hotel = inventory.getHotel();

        booking.setHotel(hotel);

        booking.setBookingReference(generateBookingReference());

        booking.setGuestName(request.getGuestName().trim());

        booking.setEmail(request.getEmail().trim());

        booking.setCheckIn(request.getCheckIn());

        booking.setCheckOut(request.getCheckOut());

        booking.setPricePerNight(pricePerNight);

        booking.setNights(nights);

        booking.setTotalAmount(totalAmount);

        booking.setStatus(BookingStatus.CONFIRMED);

        // Save booking in MySQL
        Booking savedBooking = bookingRepository.save(booking);

        // Return booking confirmation
        return convertToResponse(savedBooking);
    }


    // ==========================================
    // 2. RETRIEVE AN EXISTING BOOKING
    // ==========================================

    @Transactional(readOnly = true)
    public BookingResponse getBooking(
            String bookingReference,
            String email) {

        validateBookingCredentials(bookingReference, email);

        Booking booking = bookingRepository
                .findByBookingReferenceAndEmailIgnoreCase(
                        bookingReference,
                        email.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException("Booking not found.")
                );

        return convertToResponse(booking);
    }


    // ==========================================
    // 3. CANCEL AN EXISTING BOOKING
    // ==========================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse cancelBooking(
            String bookingReference,
            String email) {

        validateBookingCredentials(bookingReference, email);

        /*
         * First find the booking to identify its hotel.
         * We need the hotel ID to lock its inventory.
         */
        Booking existingBooking = bookingRepository
                .findByBookingReferenceAndEmailIgnoreCase(
                        bookingReference,
                        email.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException("Booking not found.")
                );

        Long hotelId = existingBooking.getHotel().getHotelId();

        // Lock the inventory before updating booking status
        roomInventoryRepository.findByHotelIdForUpdate(hotelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Room inventory not found."
                        )
                );

        // Lock the booking and verify its ownership again
        Booking booking = bookingRepository
                .findByReferenceAndEmailForUpdate(
                        bookingReference,
                        email.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException("Booking not found.")
                );

        // Prevent duplicate cancellation processing
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return convertToResponse(booking);
        }

        // Update booking status instead of deleting the record
        booking.setStatus(BookingStatus.CANCELLED);

        Booking cancelledBooking = bookingRepository.save(booking);

        return convertToResponse(cancelledBooking);
    }


    // ==========================================
    // 4. VALIDATE BOOKING DATES
    // ==========================================

    private void validateStayDates(
            LocalDate checkIn,
            LocalDate checkOut) {

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required."
            );
        }

        LocalDate today = LocalDate.now(clock);

        if (checkIn.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past."
            );
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date."
            );
        }
    }


    // ==========================================
    // 5. VALIDATE BOOKING REFERENCE AND EMAIL
    // ==========================================

    private void validateBookingCredentials(
            String bookingReference,
            String email) {

        if (bookingReference == null ||
                bookingReference.isBlank() ||
                email == null ||
                email.isBlank()) {

            throw new NoSuchElementException("Booking not found.");
        }
    }


    // ==========================================
    // 6. GENERATE UNIQUE BOOKING REFERENCE
    // ==========================================

    private String generateBookingReference() {

        String reference;

        do {
            String randomPart = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 16)
                    .toUpperCase(Locale.ROOT);

            reference = "STAY-" + randomPart;

        } while (bookingRepository.existsByBookingReference(reference));

        return reference;
    }


    // ==========================================
    // 7. CONVERT BOOKING ENTITY TO RESPONSE DTO
    // ==========================================

    private BookingResponse convertToResponse(Booking booking) {

        Hotel hotel = booking.getHotel();

        return new BookingResponse(
                booking.getBookingReference(),
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getLocation(),
                booking.getGuestName(),
                booking.getEmail(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNights(),
                booking.getPricePerNight(),
                booking.getTotalAmount(),
                booking.getStatus()
        );
    }
}
