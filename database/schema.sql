
CREATE DATABASE IF NOT EXISTS stayease_db;

USE stayease_db;

-- =========================================
-- 1. HOTELS TABLE
-- Stores basic hotel information
-- =========================================

CREATE TABLE IF NOT EXISTS hotels (
    hotel_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(150) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- 2. ROOM INVENTORY TABLE
-- Stores room count and price
-- Each hotel has one inventory record
-- =========================================

CREATE TABLE IF NOT EXISTS room_inventory (
    inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL UNIQUE,
    room_type VARCHAR(100) NOT NULL DEFAULT 'Standard Room',
    total_rooms INT NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_inventory_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels(hotel_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_total_rooms
        CHECK (total_rooms > 0),

    CONSTRAINT chk_room_price
        CHECK (price_per_night >= 0)
);

-- =========================================
-- 3. HOTEL AMENITIES TABLE
-- Stores amenities separately
-- One hotel can have many amenities
-- =========================================

CREATE TABLE IF NOT EXISTS hotel_amenities (
    amenity_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    amenity_name VARCHAR(100) NOT NULL,

    CONSTRAINT fk_amenity_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels(hotel_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_hotel_amenity
        UNIQUE (hotel_id, amenity_name)
);

-- =========================================
-- 4. BOOKINGS TABLE
-- Stores guest reservation information
-- =========================================

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    booking_reference VARCHAR(30) NOT NULL UNIQUE,

    hotel_id BIGINT NOT NULL,

    guest_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL,

    check_in DATE NOT NULL,
    check_out DATE NOT NULL,

    -- Price snapshot at booking time
    price_per_night DECIMAL(10,2) NOT NULL,
    nights INT NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels(hotel_id),

    CONSTRAINT chk_booking_dates
        CHECK (check_out > check_in),

    CONSTRAINT chk_booking_nights
        CHECK (nights > 0),

    CONSTRAINT chk_booking_price
        CHECK (price_per_night >= 0),

    CONSTRAINT chk_booking_total
        CHECK (total_amount >= 0),

    CONSTRAINT chk_booking_status
        CHECK (status IN ('CONFIRMED', 'CANCELLED'))
);

-- =========================================
-- 5. INDEXES
-- Improve searching and booking availability
-- =========================================

CREATE INDEX idx_hotel_location
    ON hotels(location);

CREATE INDEX idx_amenity_name
    ON hotel_amenities(amenity_name);

CREATE INDEX idx_booking_overlap
    ON bookings(hotel_id, status, check_in, check_out);

-- =========================================
-- DATABASE CREATED
-- =========================================
