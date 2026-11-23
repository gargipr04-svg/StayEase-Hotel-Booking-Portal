package com.stayease.backend.service;

// DTO imports
import com.stayease.backend.dto.HotelResponse;
import com.stayease.backend.dto.SmartStayMatchRequest;
import com.stayease.backend.dto.SmartStayMatchResponse;

// Entity imports
import com.stayease.backend.entity.Hotel;
import com.stayease.backend.entity.HotelAmenity;

// Repository imports
import com.stayease.backend.repository.HotelRepository;
import com.stayease.backend.repository.HotelAmenityRepository;

// Spring imports
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Java imports
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SmartStayMatchService {

    private final HotelRepository hotelRepository;
    private final HotelAmenityRepository hotelAmenityRepository;
    private final HotelService hotelService;
    private final Clock clock;

    // Constructor injection
    public SmartStayMatchService(
            HotelRepository hotelRepository,
            HotelAmenityRepository hotelAmenityRepository,
            HotelService hotelService,
            Clock clock) {

        this.hotelRepository = hotelRepository;
        this.hotelAmenityRepository = hotelAmenityRepository;
        this.hotelService = hotelService;
        this.clock = clock;
    }

    // ==========================================
    // 1. GENERATE HOTEL RECOMMENDATIONS
    // ==========================================

    public List<SmartStayMatchResponse> findBestMatches(
            SmartStayMatchRequest request) {

        validateRequest(request);

        // Normalize customer preferences
        Set<String> requestedAmenities = normalizeAmenities(
                request.getAmenities()
        );

        // Retrieve all hotels
        List<Hotel> hotels = hotelRepository.findAll();

        List<SmartStayMatchResponse> recommendations =
                new ArrayList<>();

        for (Hotel hotel : hotels) {

            // Check availability for requested dates
            int availableRooms = hotelService.getAvailableRooms(
                    hotel.getHotelId(),
                    request.getCheckIn(),
                    request.getCheckOut()
            );

            // Exclude hotels with no available rooms
            if (availableRooms <= 0) {
                continue;
            }

            // Retrieve current hotel information
            HotelResponse hotelResponse =
                    hotelService.getHotelById(hotel.getHotelId());

            // Calculate individual scores
            BigDecimal budgetScore = calculateBudgetScore(
                    hotelResponse.getPricePerNight(),
                    request.getBudget()
            );

            BigDecimal locationScore = calculateLocationScore(
                    hotel.getLocation(),
                    request.getLocation()
            );

            BigDecimal amenitiesScore = calculateAmenitiesScore(
                    hotel.getHotelId(),
                    requestedAmenities
            );

            // Calculate final weighted match score
            BigDecimal matchScore = calculateFinalScore(
                    budgetScore,
                    locationScore,
                    amenitiesScore
            );

            // Generate recommendation reasons
            List<String> reasons = generateReasons(
                    hotelResponse,
                    request,
                    requestedAmenities,
                    budgetScore,
                    locationScore
            );

            // Create recommendation response
            SmartStayMatchResponse response =
                    new SmartStayMatchResponse(
                            hotelResponse,
                            matchScore,
                            budgetScore,
                            locationScore,
                            amenitiesScore,
                            availableRooms,
                            reasons
                    );

            recommendations.add(response);
        }

        // Sort recommendations from highest score to lowest
        recommendations.sort(
                Comparator.comparing(
                                SmartStayMatchResponse::getMatchScore
                        ).reversed()
                        .thenComparing(
                                response -> response.getHotel().getPricePerNight()
                        )
        );

        return recommendations;
    }


    // ==========================================
    // 2. CALCULATE BUDGET SCORE
    // ==========================================

    private BigDecimal calculateBudgetScore(
            BigDecimal hotelPrice,
            BigDecimal budget) {

        BigDecimal maximumScore = new BigDecimal("100");

        // Full score when hotel is within budget
        if (hotelPrice.compareTo(budget) <= 0) {
            return maximumScore;
        }

        // Calculate percentage above budget
        BigDecimal overspendPercentage = hotelPrice
                .subtract(budget)
                .multiply(new BigDecimal("100"))
                .divide(budget, 4, RoundingMode.HALF_UP);

        // Reduce score by 2 points per 1% overspend
        BigDecimal penalty = overspendPercentage.multiply(
                new BigDecimal("2")
        );

        BigDecimal score = maximumScore.subtract(penalty);

        // Score cannot be negative
        if (score.compareTo(BigDecimal.ZERO) < 0) {
            score = BigDecimal.ZERO;
        }

        return score.setScale(2, RoundingMode.HALF_UP);
    }


    // ==========================================
    // 3. CALCULATE LOCATION SCORE
    // ==========================================

    private BigDecimal calculateLocationScore(
            String hotelLocation,
            String preferredLocation) {

        if (hotelLocation.equalsIgnoreCase(preferredLocation.trim())) {
            return new BigDecimal("100");
        }

        return BigDecimal.ZERO;
    }


    // ==========================================
    // 4. CALCULATE AMENITIES SCORE
    // ==========================================

    private BigDecimal calculateAmenitiesScore(
            Long hotelId,
            Set<String> requestedAmenities) {

        // No amenity preference means no penalty
        if (requestedAmenities.isEmpty()) {
            return new BigDecimal("100");
        }

        List<HotelAmenity> hotelAmenities =
                hotelAmenityRepository.findByHotel_HotelId(hotelId);

        // Normalize actual hotel amenities
        Set<String> availableAmenities = new HashSet<>();

        for (HotelAmenity amenity : hotelAmenities) {
            availableAmenities.add(
                    amenity.getAmenityName()
                            .trim()
                            .toLowerCase(Locale.ROOT)
            );
        }

        // Count matching amenities
        long matchedCount = requestedAmenities.stream()
                .filter(availableAmenities::contains)
                .count();

        // Calculate percentage match
        return BigDecimal.valueOf(matchedCount)
                .multiply(new BigDecimal("100"))
                .divide(
                        BigDecimal.valueOf(requestedAmenities.size()),
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // ==========================================
    // 5. CALCULATE FINAL WEIGHTED SCORE
    // ==========================================

    private BigDecimal calculateFinalScore(
            BigDecimal budgetScore,
            BigDecimal locationScore,
            BigDecimal amenitiesScore) {

        BigDecimal budgetContribution = budgetScore.multiply(
                new BigDecimal("0.40")
        );

        BigDecimal locationContribution = locationScore.multiply(
                new BigDecimal("0.30")
        );

        BigDecimal amenitiesContribution = amenitiesScore.multiply(
                new BigDecimal("0.30")
        );

        return budgetContribution
                .add(locationContribution)
                .add(amenitiesContribution)
                .setScale(2, RoundingMode.HALF_UP);
    }


    // ==========================================
    // 6. GENERATE RECOMMENDATION REASONS
    // ==========================================

    private List<String> generateReasons(
            HotelResponse hotel,
            SmartStayMatchRequest request,
            Set<String> requestedAmenities,
            BigDecimal budgetScore,
            BigDecimal locationScore) {

        List<String> reasons = new ArrayList<>();

        // Budget reason
        if (budgetScore.compareTo(new BigDecimal("100")) == 0) {
            reasons.add("Within budget");
        } else {
            reasons.add("Above your preferred budget");
        }

        // Location reason
        if (locationScore.compareTo(new BigDecimal("100")) == 0) {
            reasons.add("Exact location match");
        } else {
            reasons.add(
                    "Different location: " + hotel.getLocation()
            );
        }

        // Amenity reason
        if (requestedAmenities.isEmpty()) {

            reasons.add("No specific amenities requested");

        } else {

            Set<String> actualAmenities = normalizeAmenities(
                    hotel.getAmenities()
            );

            Set<String> matchedAmenities = new HashSet<>(
                    requestedAmenities
            );

            matchedAmenities.retainAll(actualAmenities);

            reasons.add(
                    matchedAmenities.size() + " of "
                            + requestedAmenities.size()
                            + " amenities matched"
            );
        }

        return reasons;
    }


    // ==========================================
    // 7. NORMALIZE AMENITY NAMES
    // ==========================================

    private Set<String> normalizeAmenities(List<String> amenities) {

        if (amenities == null) {
            return Set.of();
        }

        Set<String> normalized = new HashSet<>();

        for (String amenity : amenities) {

            if (amenity != null && !amenity.isBlank()) {
                normalized.add(
                        amenity.trim().toLowerCase(Locale.ROOT)
                );
            }
        }

        return normalized;
    }


    // ==========================================
    // 8. VALIDATE CUSTOMER REQUEST
    // ==========================================

    private void validateRequest(SmartStayMatchRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Recommendation request is required."
            );
        }

        if (request.getBudget() == null ||
                request.getBudget().compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Budget must be greater than zero."
            );
        }

        if (request.getLocation() == null ||
                request.getLocation().isBlank()) {

            throw new IllegalArgumentException(
                    "Preferred location is required."
            );
        }

        if (request.getCheckIn() == null ||
                request.getCheckOut() == null) {

            throw new IllegalArgumentException(
                    "Check-in and check-out dates are required."
            );
        }

        LocalDate today = LocalDate.now(clock);

        if (request.getCheckIn().isBefore(today)) {
            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past."
            );
        }

        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException(
                    "Check-out date must be after check-in date."
            );
        }
    }
}
