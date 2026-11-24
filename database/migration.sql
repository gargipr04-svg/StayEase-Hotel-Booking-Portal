USE stayease_db;

-- =============================================
-- SECTION 1: EXPAND HOTEL DETAILS
-- =============================================

ALTER TABLE hotels
ADD COLUMN highlights TEXT NULL,
ADD COLUMN nearby_attractions TEXT NULL,
ADD COLUMN ideal_for VARCHAR(500) NULL;


-- =============================================
-- SECTION 2: ADD ROOM OCCUPANCY LIMITS
-- =============================================

ALTER TABLE room_inventory
ADD COLUMN max_adults_per_room INT NOT NULL DEFAULT 2,
ADD COLUMN max_children_per_room INT NOT NULL DEFAULT 2,
ADD COLUMN max_occupancy INT NOT NULL DEFAULT 4;


-- =============================================
-- SECTION 3: CREATE MEAL PACKAGES
-- =============================================

CREATE TABLE meal_packages (
    package_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    package_name VARCHAR(100) NOT NULL,
    includes_breakfast BOOLEAN NOT NULL DEFAULT FALSE,
    includes_lunch BOOLEAN NOT NULL DEFAULT FALSE,
    includes_dinner BOOLEAN NOT NULL DEFAULT FALSE,
    price_per_adult_per_night DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_meal_package_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels(hotel_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_hotel_meal_package
        UNIQUE (hotel_id, package_name),

    CONSTRAINT chk_meal_package_price
        CHECK (price_per_adult_per_night >= 0)
);


-- =============================================
-- SECTION 4: UPDATE BOOKINGS
-- =============================================

ALTER TABLE bookings
ADD COLUMN rooms_count INT NOT NULL DEFAULT 1,
ADD COLUMN adults_count INT NOT NULL DEFAULT 1,
ADD COLUMN children_5_to_11_count INT NOT NULL DEFAULT 0,
ADD COLUMN children_under_5_count INT NOT NULL DEFAULT 0,
ADD COLUMN meal_package_id BIGINT NULL,
ADD COLUMN meal_price_per_adult_per_night DECIMAL(10,2) NOT NULL DEFAULT 0,
ADD COLUMN room_total DECIMAL(12,2) NOT NULL DEFAULT 0,
ADD COLUMN meal_total DECIMAL(12,2) NOT NULL DEFAULT 0,

ADD CONSTRAINT fk_booking_meal_package
    FOREIGN KEY (meal_package_id)
    REFERENCES meal_packages(package_id)
    ON DELETE SET NULL,

ADD CONSTRAINT chk_booking_rooms_count
    CHECK (rooms_count > 0),

ADD CONSTRAINT chk_booking_adults
    CHECK (adults_count >= rooms_count),

ADD CONSTRAINT chk_booking_children_5_11
    CHECK (children_5_to_11_count >= 0),

ADD CONSTRAINT chk_booking_children_under_5
    CHECK (children_under_5_count >= 0),

ADD CONSTRAINT chk_booking_meal_price
    CHECK (meal_price_per_adult_per_night >= 0),

ADD CONSTRAINT chk_booking_room_total
    CHECK (room_total >= 0),

ADD CONSTRAINT chk_booking_meal_total
    CHECK (meal_total >= 0);


-- =============================================
-- SECTION 5: STORE ROOM-WISE GUEST ALLOCATION
-- =============================================

CREATE TABLE booking_rooms (
    booking_room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    room_number INT NOT NULL,
    adults_count INT NOT NULL,
    children_5_to_11_count INT NOT NULL DEFAULT 0,
    children_under_5_count INT NOT NULL DEFAULT 0,

    CONSTRAINT fk_booking_rooms_booking
        FOREIGN KEY (booking_id)
        REFERENCES bookings(booking_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_booking_room_number
        UNIQUE (booking_id, room_number),

    CONSTRAINT chk_room_number
        CHECK (room_number > 0),

    CONSTRAINT chk_room_adults
        CHECK (adults_count >= 1),

    CONSTRAINT chk_room_children_5_11
        CHECK (children_5_to_11_count >= 0),

    CONSTRAINT chk_room_children_under_5
        CHECK (children_under_5_count >= 0)
);


-- =============================================
-- SECTION 6: ADD MEAL PACKAGES TO EXISTING HOTELS
-- =============================================

-- Ocean Pearl Resort
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Ocean Pearl Resort';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast + Lunch', TRUE, TRUE, FALSE, 300
FROM hotels WHERE name = 'Ocean Pearl Resort';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'All Meals', TRUE, TRUE, TRUE, 600
FROM hotels WHERE name = 'Ocean Pearl Resort';


-- Palm Residency
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Palm Residency';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast + Lunch', TRUE, TRUE, FALSE, 250
FROM hotels WHERE name = 'Palm Residency';


-- Cityscape Grand Hotel
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Cityscape Grand Hotel';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'All Meals', TRUE, TRUE, TRUE, 750
FROM hotels WHERE name = 'Cityscape Grand Hotel';


-- Royal Orchid Stay
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Royal Orchid Stay';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast + Lunch', TRUE, TRUE, FALSE, 280
FROM hotels WHERE name = 'Royal Orchid Stay';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'All Meals', TRUE, TRUE, TRUE, 550
FROM hotels WHERE name = 'Royal Orchid Stay';


-- Mountain View Retreat
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Mountain View Retreat';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast + Lunch', TRUE, TRUE, FALSE, 350
FROM hotels WHERE name = 'Mountain View Retreat';

INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'All Meals', TRUE, TRUE, TRUE, 650
FROM hotels WHERE name = 'Mountain View Retreat';


-- Sunrise Comfort Inn
INSERT INTO meal_packages
(hotel_id, package_name, includes_breakfast, includes_lunch,
 includes_dinner, price_per_adult_per_night)
SELECT hotel_id, 'Breakfast Only', TRUE, FALSE, FALSE, 0
FROM hotels WHERE name = 'Sunrise Comfort Inn';


-- =============================================
-- MIGRATION COMPLETE
-- =============================================
