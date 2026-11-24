package com.stayease.backend.service;

import com.stayease.backend.dto.HotelResponse;
import com.stayease.backend.dto.MealPackageResponse;
import com.stayease.backend.dto.SearchRequest;

import com.stayease.backend.entity.BookingStatus;
import com.stayease.backend.entity.Hotel;
import com.stayease.backend.entity.HotelAmenity;
import com.stayease.backend.entity.MealPackage;
import com.stayease.backend.entity.RoomInventory;

import com.stayease.backend.repository.HotelRepository;
import com.stayease.backend.repository.HotelAmenityRepository;
import com.stayease.backend.repository.RoomInventoryRepository;
import com.stayease.backend.repository.BookingRepository;
import com.stayease.backend.repository.MealPackageRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final HotelAmenityRepository hotelAmenityRepository;
    private final BookingRepository bookingRepository;
    private final MealPackageRepository mealPackageRepository;
    private final Clock clock;

    // Constructor injection
    public HotelService(
            HotelRepository hotelRepository,
            RoomInventoryRepository roomInventoryRepository,
            HotelAmenityRepository hotelAmenityRepository,
            BookingRepository bookingRepository,
            MealPackageRepository mealPackageRepository,
            Clock clock) {

        this.hotelRepository = hotelRepository;
        this.roomInventoryRepository = roomInventoryRepository;
        this.hotelAmenityRepository = hotelAmenityRepository;
        this.bookingRepository = bookingRepository;
        this.mealPackageRepository = mealPackageRepository;
        this.clock = clock;
    }

    // ==========================================
    // 1. RETRIEVE ALL HOTELS
    // ==========================================

    public List<HotelResponse> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ==========================================
    // 2. RETRIEVE HOTEL USING ITS ID
    // ==========================================

    public HotelResponse getHotelById(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Hotel not found with ID: " + hotelId
                        )
                );

        return convertToResponse(hotel);
    }

    // ==========================================
    // 3. SEARCH HOTELS BY LOCATION AND DATES
    // ==========================================

    public List<HotelResponse> searchHotels(SearchRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Search details are required."
            );
        }

        validateStayDates(
                request.getCheckIn(),
                request.getCheckOut()
        );

        List<Hotel> hotels;

        if (request.getLocation() == null ||
                request.getLocation().isBlank()) {

            hotels = hotelRepository.findAll();

        } else {

            hotels = hotelRepository
                    .findByLocationContainingIgnoreCase(
                            request.getLocation().trim()
                    );
        }

        return hotels.stream()
                .filter(hotel ->
                        calculateAvailableRooms(
                                hotel.getHotelId(),
                                request.getCheckIn(),
                                request.getCheckOut()
                        ) > 0
                )
                .map(this::convertToResponse)
                .toList();
    }

    // ==========================================
    // 4. CHECK AVAILABLE ROOMS FOR A HOTEL
    // ==========================================

    public int getAvailableRooms(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut) {

        if (hotelId == null || hotelId <= 0) {
            throw new IllegalArgumentException(
                    "Valid hotel ID is required."
            );
        }

        validateStayDates(checkIn, checkOut);

        if (!hotelRepository.existsById(hotelId)) {
            throw new NoSuchElementException(
                    "Hotel not found with ID: " + hotelId
            );
        }

        return calculateAvailableRooms(
                hotelId,
                checkIn,
                checkOut
        );
    }

    // ==========================================
    // 5. CALCULATE REMAINING ROOMS
    // ==========================================

    private int calculateAvailableRooms(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut) {

        RoomInventory inventory = roomInventoryRepository
                .findByHotel_HotelId(hotelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Room inventory not found for hotel: " + hotelId
                        )
                );

        // Count all rooms in overlapping confirmed bookings.
        // A booking can contain multiple rooms.

        long bookedRooms = bookingRepository.sumOverlappingRooms(
                hotelId,
                BookingStatus.CONFIRMED,
                checkIn,
                checkOut
        );

        long availableRooms = inventory.getTotalRooms() - bookedRooms;

        return (int) Math.max(0, availableRooms);
    }

    // ==========================================
    // 6. RETRIEVE ALL HOTEL LOCATIONS
    // ==========================================

    public List<String> getAllLocations() {

        return hotelRepository.findAllDistinctLocations();
    }

    // ==========================================
    // 7. RETRIEVE AVAILABLE MEAL PACKAGES
    // ==========================================

    public List<MealPackageResponse> getMealPackages(Long hotelId) {

        if (hotelId == null || hotelId <= 0) {
            throw new IllegalArgumentException(
                    "Valid hotel ID is required."
            );
        }

        if (!hotelRepository.existsById(hotelId)) {
            throw new NoSuchElementException(
                    "Hotel not found with ID: " + hotelId
            );
        }

        return mealPackageRepository
                .findByHotel_HotelIdAndAvailableTrue(hotelId)
                .stream()
                .map(this::convertToMealPackageResponse)
                .toList();
    }

    // ==========================================
    // 8. CONVERT MEAL PACKAGE INTO RESPONSE DTO
    // ==========================================

    private MealPackageResponse convertToMealPackageResponse(
            MealPackage mealPackage) {

        return new MealPackageResponse(
                mealPackage.getPackageId(),
                mealPackage.getPackageName(),
                mealPackage.isIncludesBreakfast(),
                mealPackage.isIncludesLunch(),
                mealPackage.isIncludesDinner(),
                mealPackage.getPricePerAdultPerNight()
        );
    }

    // ==========================================
    // 9. VALIDATE BOOKING DATES
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
    // 10. CONVERT HOTEL ENTITY INTO RESPONSE DTO
    // ==========================================

    private HotelResponse convertToResponse(Hotel hotel) {

        RoomInventory inventory = roomInventoryRepository
                .findByHotel_HotelId(hotel.getHotelId())
                .orElse(null);

        List<String> amenities = hotelAmenityRepository
                .findByHotel_HotelId(hotel.getHotelId())
                .stream()
                .map(HotelAmenity::getAmenityName)
                .toList();

        return new HotelResponse(

                // Basic hotel information
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getImageUrl(),

                // Additional hotel information
                hotel.getHighlights(),
                hotel.getNearbyAttractions(),
                hotel.getIdealFor(),

                // Room information
                inventory != null ? inventory.getRoomType() : null,
                inventory != null ? inventory.getTotalRooms() : null,
                inventory != null ? inventory.getPricePerNight() : null,

                // Room occupancy limits
                inventory != null ? inventory.getMaxAdultsPerRoom() : null,
                inventory != null ? inventory.getMaxChildrenPerRoom() : null,
                inventory != null ? inventory.getMaxOccupancy() : null,

                // Hotel amenities
                amenities
        );
    }
}
