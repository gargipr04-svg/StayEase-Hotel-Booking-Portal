USE stayease_db;

-- =========================================
-- INSERT SAMPLE HOTELS
-- =========================================

INSERT INTO hotels
(name, location, description, image_url)
VALUES

(
    'Ocean Pearl Resort',
    'Goa',
    'A relaxing beachside resort with comfortable rooms and recreational facilities.',
    'https://images.unsplash.com/photo-1571896349842-33c89424de2d'
),

(
    'Palm Residency',
    'Goa',
    'A budget-friendly hotel suitable for families and leisure travellers.',
    'https://images.unsplash.com/photo-1566073771259-6a8506099945'
),

(
    'Cityscape Grand Hotel',
    'Mumbai',
    'A modern city hotel with business facilities and easy access to city attractions.',
    'https://images.unsplash.com/photo-1564501049412-61c2a3083791'
),

(
    'Royal Orchid Stay',
    'Pune',
    'A comfortable hotel offering modern amenities for business and leisure stays.',
    'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb'
),

(
    'Mountain View Retreat',
    'Lonavala',
    'A peaceful hillside retreat with scenic surroundings and spacious accommodation.',
    'https://images.unsplash.com/photo-1518780664697-55e3ad937233'
),

(
    'Sunrise Comfort Inn',
    'Mumbai',
    'A simple and affordable hotel offering essential facilities and comfortable rooms.',
    'https://images.unsplash.com/photo-1445019980597-93fa8acb246c'
);

-- =========================================
-- INSERT ROOM INVENTORY
-- One inventory record for each hotel
-- =========================================

INSERT INTO room_inventory
(hotel_id, room_type, total_rooms, price_per_night)
VALUES
(1, 'Deluxe Room', 20, 3500.00),
(2, 'Standard Room', 15, 2200.00),
(3, 'Executive Room', 25, 5200.00),
(4, 'Standard Room', 18, 3000.00),
(5, 'Mountain View Room', 12, 4500.00),
(6, 'Standard Room', 10, 1800.00);

-- =========================================
-- INSERT HOTEL AMENITIES
-- =========================================

INSERT INTO hotel_amenities
(hotel_id, amenity_name)
VALUES

-- Ocean Pearl Resort
(1, 'Wi-Fi'),
(1, 'Swimming Pool'),
(1, 'AC'),
(1, 'Parking'),
(1, 'Restaurant'),
(1, 'Beach Access'),

-- Palm Residency
(2, 'Wi-Fi'),
(2, 'AC'),
(2, 'Parking'),
(2, 'Restaurant'),

-- Cityscape Grand Hotel
(3, 'Wi-Fi'),
(3, 'AC'),
(3, 'Parking'),
(3, 'Restaurant'),
(3, 'Gym'),
(3, 'Conference Room'),

-- Royal Orchid Stay
(4, 'Wi-Fi'),
(4, 'AC'),
(4, 'Parking'),
(4, 'Swimming Pool'),

-- Mountain View Retreat
(5, 'Wi-Fi'),
(5, 'Swimming Pool'),
(5, 'Parking'),
(5, 'Restaurant'),
(5, 'Mountain View'),

-- Sunrise Comfort Inn
(6, 'Wi-Fi'),
(6, 'AC'),
(6, 'Parking');

-- =========================================
-- VERIFY DATA
-- =========================================

SELECT * FROM hotels;

SELECT * FROM room_inventory;

SELECT * FROM hotel_amenities;

SELECT * FROM bookings;
