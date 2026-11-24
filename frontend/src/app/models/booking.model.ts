export interface BookingRequest {
  hotelId: number;
  guestName: string;
  email: string;
  checkIn: string;
  checkOut: string;
  mealPackageId?: number | null;
  rooms: RoomAllocation[];
}

export interface RoomAllocation {
  roomNumber: number;
  adultsCount: number;
  children5To11Count: number;
  childrenUnder5Count: number;
}

export interface BookingResponse {
  bookingReference: string;
  hotelId: number;
  hotelName: string;
  location: string;
  guestName: string;
  email: string;
  checkIn: string;
  checkOut: string;
  nights: number;
  pricePerNight: number;
  totalAmount: number;
  roomsCount: number;
  adultsCount: number;
  children5To11Count: number;
  childrenUnder5Count: number;
  mealPackageName: string | null;
  mealPricePerAdultPerNight: number;
  roomTotal: number;
  mealTotal: number;
  rooms: RoomResponse[];
  status: string;
}

export interface RoomResponse {
  roomNumber: number;
  adultsCount: number;
  children5To11Count: number;
  childrenUnder5Count: number;
}
