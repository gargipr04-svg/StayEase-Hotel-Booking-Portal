package com.stayease.backend.service;

import com.stayease.backend.dto.HotelResponse;
import com.stayease.backend.dto.SearchRequest;

import com.stayease.backend.entity.BookingStatus;
import com.stayease.backend.entity.Hotel;
import com.stayease.backend.entity.HotelAmenity;
import com.stayease.backend.entity.RoomInventory;

import com.stayease.backend.repository.HotelRepository;
import com.stayease.backend.repository.HotelAmenityRepository;
import com.stayease.backend.repository.RoomInventoryRepository;
import com.stayease.backend.repository.BookingRepository;

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
    private final Clock clock;

    // Constructor injection
    public HotelService(
            HotelRepository hotelRepository,
            RoomInventoryRepository roomInventoryRepository,
            HotelAmenityRepository hotelAmenityRepository,
            BookingRepository bookingRepository,
            Clock clock) {

        this.hotelRepository = hotelRepository;
        this.roomInventoryRepository = roomInventoryRepository;
        this.hotelAmenityRepository = hotelAmenityRepository;
        this.bookingRepository = bookingRepository;
        this.clock = clock;
    }

    // 1. Retrieve all hotels
    public List<HotelResponse> getAllHotels() {

        return hotelRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // 2. Retrieve a hotel using its ID
    public HotelResponse getHotelById(Long hotelId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Hotel not found with ID: " + hotelId
                        )
                );

        return convertToResponse(hotel);
    }

    // 3. Search hotels by location and dates
    public List<HotelResponse> searchHotels(SearchRequest request) {

        validateStayDates(request.getCheckIn(), request.getCheckOut());

        List<Hotel> hotels;

        if (request.getLocation() == null ||
                request.getLocation().isBlank()) {

            hotels = hotelRepository.findAll();

        } else {

            hotels = hotelRepository
                    .findByLocationContainingIgnoreCase(
                            request.getLocation()
                    );
        }

        // Return only hotels having available rooms
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

    // 4. Check available rooms for a hotel
    public int getAvailableRooms(
            Long hotelId,
            LocalDate checkIn,
            LocalDate checkOut) {

        validateStayDates(checkIn, checkOut);

        if (!hotelRepository.existsById(hotelId)) {
            throw new NoSuchElementException(
                    "Hotel not found with ID: " + hotelId
            );
        }

        return calculateAvailableRooms(hotelId, checkIn, checkOut);
    }

    // Calculate remaining rooms
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

        long bookedRooms = bookingRepository.countOverlappingBookings(
                hotelId,
                BookingStatus.CONFIRMED,
                checkIn,
                checkOut
        );

        long availableRooms = inventory.getTotalRooms() - bookedRooms;

        return (int) Math.max(0, availableRooms);
    }

    // Validate booking dates
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

    // Convert database entity into frontend response DTO
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
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getImageUrl(),
                inventory != null ? inventory.getRoomType() : null,
                inventory != null ? inventory.getTotalRooms() : null,
                inventory != null ? inventory.getPricePerNight() : null,
                amenities
        );
    }
}
