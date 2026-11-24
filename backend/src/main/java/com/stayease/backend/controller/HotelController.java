package com.stayease.backend.controller;

// DTO imports
import com.stayease.backend.dto.HotelResponse;
import com.stayease.backend.dto.MealPackageResponse;
import com.stayease.backend.dto.SearchRequest;
import com.stayease.backend.dto.SmartStayMatchRequest;
import com.stayease.backend.dto.SmartStayMatchResponse;

// Service imports
import com.stayease.backend.service.HotelService;
import com.stayease.backend.service.SmartStayMatchService;

// Spring imports
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

// Java imports
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final SmartStayMatchService smartStayMatchService;

    // Constructor injection
    public HotelController(
            HotelService hotelService,
            SmartStayMatchService smartStayMatchService) {

        this.hotelService = hotelService;
        this.smartStayMatchService = smartStayMatchService;
    }

    // ==========================================
    // 1. GET ALL HOTELS
    // GET /api/hotels
    // ==========================================

    @GetMapping
    public List<HotelResponse> getAllHotels() {

        return hotelService.getAllHotels();
    }

    // ==========================================
    // 2. GET HOTEL BY ID
    // GET /api/hotels/{hotelId}
    // ==========================================

    @GetMapping("/{hotelId}")
    public HotelResponse getHotelById(
            @PathVariable Long hotelId) {

        return hotelService.getHotelById(hotelId);
    }

    // ==========================================
    // 3. GET ALL HOTEL LOCATIONS
    // GET /api/hotels/locations
    // ==========================================

    @GetMapping("/locations")
    public List<String> getAllLocations() {

        return hotelService.getAllLocations();
    }

    // ==========================================
    // 4. GET HOTEL MEAL PACKAGES
    // GET /api/hotels/{hotelId}/meal-packages
    // ==========================================

    @GetMapping("/{hotelId}/meal-packages")
    public List<MealPackageResponse> getMealPackages(
            @PathVariable Long hotelId) {

        return hotelService.getMealPackages(hotelId);
    }

    // ==========================================
    // 5. SEARCH HOTELS BY LOCATION AND DATES
    // GET /api/hotels/search
    // ==========================================

    @GetMapping("/search")
    public List<HotelResponse> searchHotels(
            @RequestParam(required = false) String location,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut) {

        SearchRequest request = new SearchRequest();

        request.setLocation(location);
        request.setCheckIn(checkIn);
        request.setCheckOut(checkOut);

        return hotelService.searchHotels(request);
    }

    // ==========================================
    // 6. CHECK ROOM AVAILABILITY
    // GET /api/hotels/{hotelId}/availability
    // ==========================================

    @GetMapping("/{hotelId}/availability")
    public Map<String, Object> getAvailability(

            @PathVariable Long hotelId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkIn,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOut) {

        int availableRooms = hotelService.getAvailableRooms(
                hotelId,
                checkIn,
                checkOut
        );

        return Map.of(
                "hotelId", hotelId,
                "checkIn", checkIn,
                "checkOut", checkOut,
                "availableRooms", availableRooms,
                "available", availableRooms > 0
        );
    }

    // ==========================================
    // 7. SMART STAY MATCH
    // POST /api/hotels/smart-match
    // ==========================================

    @PostMapping("/smart-match")
    public List<SmartStayMatchResponse> findBestMatches(
            @Valid @RequestBody SmartStayMatchRequest request) {

        return smartStayMatchService.findBestMatches(request);
    }
}
