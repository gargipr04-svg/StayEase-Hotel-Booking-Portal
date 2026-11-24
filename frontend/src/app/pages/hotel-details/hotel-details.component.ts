
import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ActivatedRoute,
  RouterLink
} from '@angular/router';

import { HotelService } from '../../services/hotel.service';
import { BookingService } from '../../services/booking.service';

import { Hotel } from '../../models/hotel.model';

import {
  BookingRequest,
  BookingResponse
} from '../../models/booking.model';

@Component({
  selector: 'app-hotel-details',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './hotel-details.component.html',
  styleUrl: './hotel-details.component.css'
})
export class HotelDetailsComponent implements OnInit {

  hotel: Hotel | null = null;

  isLoading = true;
  errorMessage = '';

  guestName = '';
  email = '';
  checkIn = '';
  checkOut = '';

  today = this.getTodayDate();

  bookingLoading = false;
  bookingError = '';
  bookingSuccess: BookingResponse | null = null;

  // Room availability
  availableRooms: number | null = null;
  availabilityLoading = false;
  availabilityError = '';

  constructor(
    private route: ActivatedRoute,
    private hotelService: HotelService,
    private bookingService: BookingService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    const idParam = this.route.snapshot.paramMap.get('id');
    const hotelId = Number(idParam);

    if (!idParam || !Number.isInteger(hotelId) || hotelId <= 0) {
      this.errorMessage = 'Invalid hotel ID.';
      this.isLoading = false;
      return;
    }

    this.hotelService.getHotelById(hotelId).subscribe({

      next: (data: Hotel) => {
        this.hotel = data;
        this.isLoading = false;

        console.log('Hotel details loaded:', data);

        this.changeDetectorRef.markForCheck();
      },

      error: (error) => {
        console.error('Error loading hotel:', error);

        this.errorMessage =
          'Unable to find this hotel. Please try another hotel.';

        this.isLoading = false;

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Today's date in YYYY-MM-DD format
  private getTodayDate(): string {

    const now = new Date();

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  // Checkout must be at least one day after check-in
  get minimumCheckoutDate(): string {

    if (!this.checkIn) {
      return this.today;
    }

    const [year, month, day] = this.checkIn.split('-').map(Number);

    const nextDay = new Date(year, month - 1, day);
    nextDay.setDate(nextDay.getDate() + 1);

    const nextYear = nextDay.getFullYear();
    const nextMonth = String(nextDay.getMonth() + 1).padStart(2, '0');
    const nextDate = String(nextDay.getDate()).padStart(2, '0');

    return `${nextYear}-${nextMonth}-${nextDate}`;
  }

  // Calculate number of nights
  get numberOfNights(): number {

    if (!this.checkIn || !this.checkOut ||
        this.checkOut <= this.checkIn) {
      return 0;
    }

    const start = new Date(`${this.checkIn}T00:00:00`);
    const end = new Date(`${this.checkOut}T00:00:00`);

    return Math.round(
      (end.getTime() - start.getTime()) / 86400000
    );
  }

  // Estimated total price
  get estimatedTotal(): number {
    return (this.hotel?.pricePerNight ?? 0) * this.numberOfNights;
  }

  // Fetch available rooms for selected dates
  loadAvailability(): void {

    if (!this.hotel || !this.checkIn || !this.checkOut ||
        this.checkIn < this.today ||
        this.checkOut <= this.checkIn) {

      this.availableRooms = null;
      this.availabilityError = '';
      this.changeDetectorRef.markForCheck();
      return;
    }

    this.availabilityLoading = true;
    this.availabilityError = '';

    this.hotelService.getAvailability(
      this.hotel.hotelId,
      this.checkIn,
      this.checkOut
    ).subscribe({

      next: (response) => {
        this.availableRooms = response.availableRooms;
        this.availabilityLoading = false;

        console.log('Room availability:', response);

        this.changeDetectorRef.markForCheck();
      },

      error: (error) => {
        console.error('Availability error:', error);

        this.availableRooms = null;
        this.availabilityLoading = false;
        this.availabilityError = 'Unable to check room availability.';

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Create a new reservation
  submitBooking(): void {

    this.bookingError = '';

    if (!this.hotel) {
      this.bookingError = 'Hotel information is unavailable.';
      return;
    }

    if (!this.guestName.trim() || !this.email.trim()) {
      this.bookingError = 'Please enter your name and email.';
      return;
    }

    if (this.checkIn < this.today) {
      this.bookingError = 'Check-in cannot be in the past.';
      return;
    }

    if (!this.checkOut || this.checkOut <= this.checkIn) {
      this.bookingError =
        'Check-out must be after check-in.';
      return;
    }

    if (this.availabilityLoading || this.availableRooms === null) {
  this.bookingError = 'Please wait while we check room availability.';
  return;
}

if (this.availabilityError) {
  this.bookingError = 'Unable to verify room availability. Please try again.';
  return;
}

if (this.availableRooms <= 0) {
  this.bookingError = 'No rooms are available for these dates.';
  return;
}

    const request: BookingRequest = {
      hotelId: this.hotel.hotelId,
      guestName: this.guestName.trim(),
      email: this.email.trim(),
      checkIn: this.checkIn,
      checkOut: this.checkOut
    };

    this.bookingLoading = true;

    this.bookingService.createBooking(request).subscribe({

      next: (response: BookingResponse) => {

        this.bookingSuccess = response;
        this.bookingLoading = false;

        console.log('Booking confirmed:', response);

        // Refresh availability after successful booking
        this.loadAvailability();

        this.changeDetectorRef.markForCheck();
      },

      error: (error) => {

        console.error('Booking failed:', error);

        this.bookingError =
          error?.error?.message ??
          'Unable to complete your booking. Please try again.';

        this.bookingLoading = false;

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Start a fresh booking
  bookAgain(): void {

    this.bookingSuccess = null;

    this.guestName = '';
    this.email = '';
    this.checkIn = '';
    this.checkOut = '';

    this.bookingError = '';

    this.availableRooms = null;
    this.availabilityError = '';

    this.changeDetectorRef.markForCheck();
  }
}
