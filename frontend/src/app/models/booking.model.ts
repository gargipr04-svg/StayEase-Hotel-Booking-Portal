export interface BookingRequest {
  hotelId: number;
  guestName: string;
  email: string;
  checkIn: string;
  checkOut: string;
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
  status: string;
}
