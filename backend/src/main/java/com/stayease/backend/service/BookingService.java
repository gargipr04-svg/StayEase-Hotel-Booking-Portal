package com.stayease.backend.service;

import com.stayease.backend.dto.BookingRequest;
import com.stayease.backend.dto.BookingResponse;

import com.stayease.backend.entity.Booking;
import com.stayease.backend.entity.BookingRoom;
import com.stayease.backend.entity.BookingStatus;
import com.stayease.backend.entity.Hotel;
import com.stayease.backend.entity.MealPackage;
import com.stayease.backend.entity.RoomInventory;

import com.stayease.backend.repository.BookingRepository;
import com.stayease.backend.repository.MealPackageRepository;
import com.stayease.backend.repository.RoomInventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final MealPackageRepository mealPackageRepository;
    private final Clock clock;

    public BookingService(
            BookingRepository bookingRepository,
            RoomInventoryRepository roomInventoryRepository,
            MealPackageRepository mealPackageRepository,
            Clock clock) {

        this.bookingRepository = bookingRepository;
        this.roomInventoryRepository = roomInventoryRepository;
        this.mealPackageRepository = mealPackageRepository;
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

        // Lock hotel inventory to prevent simultaneous overbooking
        RoomInventory inventory = roomInventoryRepository
                .findByHotelIdForUpdate(request.getHotelId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Hotel or room inventory not found."
                        )
                );

        // Validate room allocations
        List<BookingRequest.RoomAllocation> allocations = request.getRooms();

        if (allocations == null || allocations.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one room must be selected."
            );
        }

        int roomsCount = allocations.size();

        if (roomsCount > inventory.getTotalRooms()) {
            throw new IllegalArgumentException(
                    "Selected number of rooms exceeds the hotel's total room inventory."
            );
        }

        validateRoomAllocations(allocations, inventory);

        // Calculate total guest counts
        int adultsCount = 0;
        int children5To11Count = 0;
        int childrenUnder5Count = 0;

        for (BookingRequest.RoomAllocation allocation : allocations) {

            adultsCount += allocation.getAdultsCount();

            children5To11Count += allocation.getChildren5To11Count();

            childrenUnder5Count += allocation.getChildrenUnder5Count();
        }

        // Every room must have at least one adult
        if (adultsCount < roomsCount) {
            throw new IllegalArgumentException(
                    "Every room must have at least one adult."
            );
        }

        // Check actual available rooms across overlapping confirmed bookings
        long bookedRooms = bookingRepository.sumOverlappingRooms(
                request.getHotelId(),
                BookingStatus.CONFIRMED,
                request.getCheckIn(),
                request.getCheckOut()
        );

        long availableRooms = inventory.getTotalRooms() - bookedRooms;

        if (roomsCount > availableRooms) {
            throw new IllegalStateException(
                    "Only " + Math.max(availableRooms, 0) +
                            " room(s) are available for the selected dates."
            );
        }

        // Calculate number of nights
        long nightsLong = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        int nights = Math.toIntExact(nightsLong);

        // Get actual room price from database
        BigDecimal pricePerNight = inventory.getPricePerNight();

        if (pricePerNight == null || pricePerNight.signum() < 0) {
            throw new IllegalStateException(
                    "Invalid room price configured for this hotel."
            );
        }

        // Calculate room charges
        BigDecimal roomTotal = pricePerNight
                .multiply(BigDecimal.valueOf(roomsCount))
                .multiply(BigDecimal.valueOf(nights));

        // ==========================================
        // MEAL PACKAGE CALCULATION
        // ==========================================

        MealPackage selectedMealPackage = null;

        BigDecimal mealPricePerAdultPerNight = BigDecimal.ZERO;

        BigDecimal mealTotal = BigDecimal.ZERO;

        if (request.getMealPackageId() != null) {

            selectedMealPackage = mealPackageRepository
                    .findByHotel_HotelIdAndAvailableTrue(request.getHotelId())
                    .stream()
                    .filter(mealPackage ->
                            mealPackage.getPackageId().equals(
                                    request.getMealPackageId()
                            )
                    )
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Selected meal package is unavailable or does not belong to this hotel."
                            )
                    );

            mealPricePerAdultPerNight =
                    selectedMealPackage.getPricePerAdultPerNight();

            if (mealPricePerAdultPerNight == null ||
                    mealPricePerAdultPerNight.signum() < 0) {

                throw new IllegalStateException(
                        "Invalid meal package price configured."
                );
            }

            /*
             * Adults aged 12 and above:
             * Full meal package price.
             *
             * Children aged 5 to 11:
             * 50% of the meal package price.
             *
             * Children under 5:
             * Free meals.
             */

            BigDecimal adultMealUnits = BigDecimal.valueOf(adultsCount);

            BigDecimal childMealUnits = BigDecimal
                    .valueOf(children5To11Count)
                    .multiply(new BigDecimal("0.50"));

            BigDecimal totalMealUnits = adultMealUnits.add(childMealUnits);

            mealTotal = mealPricePerAdultPerNight
                    .multiply(totalMealUnits)
                    .multiply(BigDecimal.valueOf(nights))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Calculate final booking amount
        BigDecimal totalAmount = roomTotal.add(mealTotal);

        // ==========================================
        // CREATE BOOKING ENTITY
        // ==========================================

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

        booking.setRoomsCount(roomsCount);

        booking.setAdultsCount(adultsCount);

        booking.setChildren5To11Count(children5To11Count);

        booking.setChildrenUnder5Count(childrenUnder5Count);

        booking.setMealPackage(selectedMealPackage);

        booking.setMealPricePerAdultPerNight(
                mealPricePerAdultPerNight
        );

        booking.setRoomTotal(roomTotal);

        booking.setMealTotal(mealTotal);

        booking.setTotalAmount(totalAmount);

        booking.setStatus(BookingStatus.CONFIRMED);

        // ==========================================
        // SAVE ROOM-WISE GUEST ALLOCATION
        // ==========================================

        List<BookingRoom> bookingRooms = new ArrayList<>();

        for (BookingRequest.RoomAllocation allocation : allocations) {

            BookingRoom bookingRoom = new BookingRoom();

            bookingRoom.setBooking(booking);

            bookingRoom.setRoomNumber(allocation.getRoomNumber());

            bookingRoom.setAdultsCount(allocation.getAdultsCount());

            bookingRoom.setChildren5To11Count(
                    allocation.getChildren5To11Count()
            );

            bookingRoom.setChildrenUnder5Count(
                    allocation.getChildrenUnder5Count()
            );

            bookingRooms.add(bookingRoom);
        }

        booking.setBookingRooms(bookingRooms);

        // Cascade saves the booking rooms along with the booking
        Booking savedBooking = bookingRepository.save(booking);

        return convertToResponse(savedBooking);
    }


    // ==========================================
    // 2. VALIDATE ROOM ALLOCATIONS
    // ==========================================

    private void validateRoomAllocations(
            List<BookingRequest.RoomAllocation> allocations,
            RoomInventory inventory) {

        Set<Integer> roomNumbers = new HashSet<>();

        int maxAdults = inventory.getMaxAdultsPerRoom();

        int maxChildren = inventory.getMaxChildrenPerRoom();

        int maxOccupancy = inventory.getMaxOccupancy();

        if (maxAdults <= 0 || maxChildren < 0 || maxOccupancy <= 0) {
            throw new IllegalStateException(
                    "Invalid room occupancy limits configured."
            );
        }

        for (BookingRequest.RoomAllocation allocation : allocations) {

            if (allocation == null) {
                throw new IllegalArgumentException(
                        "Room allocation cannot be empty."
                );
            }

            Integer roomNumber = allocation.getRoomNumber();

            Integer adults = allocation.getAdultsCount();

            Integer children5To11 = allocation.getChildren5To11Count();

            Integer childrenUnder5 = allocation.getChildrenUnder5Count();

            if (roomNumber == null ||
                    roomNumber < 1 ||
                    roomNumber > allocations.size()) {

                throw new IllegalArgumentException(
                        "Room numbers must be between 1 and the selected number of rooms."
                );
            }

            if (!roomNumbers.add(roomNumber)) {
                throw new IllegalArgumentException(
                        "Duplicate room numbers are not allowed."
                );
            }

            if (adults == null || adults < 1) {
                throw new IllegalArgumentException(
                        "Every room must have at least one adult."
                );
            }

            if (children5To11 == null || children5To11 < 0 ||
                    childrenUnder5 == null || childrenUnder5 < 0) {

                throw new IllegalArgumentException(
                        "Child counts cannot be negative or missing."
                );
            }

            if (adults > maxAdults) {
                throw new IllegalArgumentException(
                        "Room " + roomNumber +
                                " exceeds the maximum adults allowed per room."
                );
            }

            int totalChildren = children5To11 + childrenUnder5;

            if (totalChildren > maxChildren) {
                throw new IllegalArgumentException(
                        "Room " + roomNumber +
                                " exceeds the maximum children allowed per room."
                );
            }

            int totalGuests = adults + totalChildren;

            if (totalGuests > maxOccupancy) {
                throw new IllegalArgumentException(
                        "Room " + roomNumber +
                                " exceeds the maximum total occupancy."
                );
            }
        }

        // Ensure room numbers are complete and consecutive
        if (roomNumbers.size() != allocations.size()) {
            throw new IllegalArgumentException(
                    "Every selected room must have a unique allocation."
            );
        }
    }


    // ==========================================
    // 3. RETRIEVE AN EXISTING BOOKING
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
    // 4. CANCEL AN EXISTING BOOKING
    // ==========================================

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse cancelBooking(
            String bookingReference,
            String email) {

        validateBookingCredentials(bookingReference, email);

        // Find booking to identify the hotel's inventory
        Booking existingBooking = bookingRepository
                .findByBookingReferenceAndEmailIgnoreCase(
                        bookingReference,
                        email.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException("Booking not found.")
                );

        Long hotelId = existingBooking.getHotel().getHotelId();

        // Lock inventory before changing booking status
        roomInventoryRepository.findByHotelIdForUpdate(hotelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Room inventory not found."
                        )
                );

        // Lock booking and verify ownership again
        Booking booking = bookingRepository
                .findByReferenceAndEmailForUpdate(
                        bookingReference,
                        email.trim()
                )
                .orElseThrow(() ->
                        new NoSuchElementException("Booking not found.")
                );

        // Prevent duplicate cancellation
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return convertToResponse(booking);
        }

        // Update status instead of deleting the booking
        booking.setStatus(BookingStatus.CANCELLED);

        Booking cancelledBooking = bookingRepository.save(booking);

        return convertToResponse(cancelledBooking);
    }


    // ==========================================
    // 5. VALIDATE BOOKING DATES
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
    // 6. VALIDATE BOOKING REFERENCE AND EMAIL
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
    // 7. GENERATE UNIQUE BOOKING REFERENCE
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
    // 8. CONVERT BOOKING ENTITY TO RESPONSE DTO
    // ==========================================

    private BookingResponse convertToResponse(Booking booking) {

        Hotel hotel = booking.getHotel();

        String mealPackageName = booking.getMealPackage() == null
                ? null
                : booking.getMealPackage().getPackageName();

        List<BookingResponse.RoomResponse> roomResponses =
                booking.getBookingRooms()
                        .stream()
                        .sorted((first, second) ->
                                first.getRoomNumber().compareTo(
                                        second.getRoomNumber()
                                )
                        )
                        .map(room -> new BookingResponse.RoomResponse(
                                room.getRoomNumber(),
                                room.getAdultsCount(),
                                room.getChildren5To11Count(),
                                room.getChildrenUnder5Count()
                        ))
                        .toList();

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
                booking.getRoomsCount(),
                booking.getAdultsCount(),
                booking.getChildren5To11Count(),
                booking.getChildrenUnder5Count(),
                mealPackageName,
                booking.getMealPricePerAdultPerNight(),
                booking.getRoomTotal(),
                booking.getMealTotal(),
                roomResponses,
                booking.getStatus()
        );
    }
}
