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

import {
  HotelService,
  MealPackage
} from '../../services/hotel.service';

import { BookingService } from '../../services/booking.service';

import { Hotel } from '../../models/hotel.model';

import {
  BookingRequest,
  BookingResponse,
  RoomAllocation
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

  // Room allocation
  rooms: RoomAllocation[] = [
    {
      roomNumber: 1,
      adultsCount: 1,
      children5To11Count: 0,
      childrenUnder5Count: 0
    }
  ];

  // Meal packages
  mealPackages: MealPackage[] = [];
  selectedMealPackageId: number | null = null;
  mealPackagesLoading = false;
  mealPackagesError = '';

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

        this.loadMealPackages();

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

  // Hotel occupancy limits
  get maxAdultsPerRoom(): number {
    return this.hotel?.maxAdultsPerRoom ?? 2;
  }

  get maxChildrenPerRoom(): number {
    return this.hotel?.maxChildrenPerRoom ?? 2;
  }

  get maxOccupancy(): number {
    return this.hotel?.maxOccupancy ?? 4;
  }

  // Total adults across all rooms
  get totalAdults(): number {
    return this.rooms.reduce(
      (total, room) => total + room.adultsCount,
      0
    );
  }

  // Total children aged 5 to 11
  get totalChildren5To11(): number {
    return this.rooms.reduce(
      (total, room) => total + room.children5To11Count,
      0
    );
  }

  // Total children under 5
  get totalChildrenUnder5(): number {
    return this.rooms.reduce(
      (total, room) => total + room.childrenUnder5Count,
      0
    );
  }

  // Total children
  get totalChildren(): number {
    return this.totalChildren5To11 + this.totalChildrenUnder5;
  }

  // Total guests
  get totalGuests(): number {
    return this.totalAdults + this.totalChildren;
  }

  // Selected meal package
  get selectedMealPackage(): MealPackage | null {

    return this.mealPackages.find(
      item => item.packageId === this.selectedMealPackageId
    ) ?? null;
  }

  // Room price for the entire stay
  get roomTotal(): number {

    return (this.hotel?.pricePerNight ?? 0) *
      this.rooms.length *
      this.numberOfNights;
  }

  // Meal price for the entire stay
  get mealTotal(): number {

    if (!this.selectedMealPackage) {
      return 0;
    }

    const chargeableGuests =
      this.totalAdults + (this.totalChildren5To11 * 0.5);

    return chargeableGuests *
      this.selectedMealPackage.pricePerAdultPerNight *
      this.numberOfNights;
  }

  // Complete estimated booking price
  get estimatedTotal(): number {
    return this.roomTotal + this.mealTotal;
  }

  // Load hotel-specific meal packages
  loadMealPackages(): void {

    if (!this.hotel) {
      return;
    }

    this.mealPackagesLoading = true;
    this.mealPackagesError = '';

    this.hotelService.getMealPackages(
      this.hotel.hotelId
    ).subscribe({

      next: (packages: MealPackage[]) => {

        this.mealPackages = packages;
        this.mealPackagesLoading = false;

        this.selectedMealPackageId = null;

        console.log('Meal packages loaded:', packages);

        this.changeDetectorRef.markForCheck();
      },

      error: (error) => {

        console.error('Meal package error:', error);

        this.mealPackages = [];
        this.mealPackagesLoading = false;

        this.mealPackagesError =
          'Unable to load meal packages.';

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Add another room
  addRoom(): void {

    if (this.availableRooms === null ||
        this.rooms.length >= this.availableRooms) {
      return;
    }

    this.rooms.push({
      roomNumber: this.rooms.length + 1,
      adultsCount: 1,
      children5To11Count: 0,
      childrenUnder5Count: 0
    });

    this.changeDetectorRef.markForCheck();
  }

  // Remove a room
  removeRoom(index: number): void {

    if (this.rooms.length <= 1) {
      return;
    }

    this.rooms.splice(index, 1);

    this.rooms.forEach((room, i) => {
      room.roomNumber = i + 1;
    });

    this.changeDetectorRef.markForCheck();
  }

  // Get total occupants in one room
  getRoomOccupancy(room: RoomAllocation): number {

    return room.adultsCount +
      room.children5To11Count +
      room.childrenUnder5Count;
  }

  // Check whether a room has valid occupancy
  isRoomValid(room: RoomAllocation): boolean {

    return room.adultsCount >= 1 &&
      room.adultsCount <= this.maxAdultsPerRoom &&
      room.children5To11Count >= 0 &&
      room.childrenUnder5Count >= 0 &&
      room.children5To11Count + room.childrenUnder5Count
        <= this.maxChildrenPerRoom &&
      this.getRoomOccupancy(room) <= this.maxOccupancy;
  }

  // Validate all room allocations
  get areRoomsValid(): boolean {

    return this.rooms.length > 0 &&
      this.rooms.every(room => this.isRoomValid(room));
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
    this.availableRooms = null;

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

        this.availabilityError =
          'Unable to check room availability.';

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

    if (!this.areRoomsValid) {
      this.bookingError =
        'Please check the guest allocation for every room.';
      return;
    }

    if (this.availabilityLoading || this.availableRooms === null) {
      this.bookingError =
        'Please wait while we check room availability.';
      return;
    }

    if (this.availabilityError) {
      this.bookingError =
        'Unable to verify room availability. Please try again.';
      return;
    }

    if (this.availableRooms <= 0) {
      this.bookingError =
        'No rooms are available for these dates.';
      return;
    }

    if (this.rooms.length > this.availableRooms) {
      this.bookingError =
        `Only ${this.availableRooms} rooms are available.`;
      return;
    }

    if (this.mealPackagesLoading) {
      this.bookingError =
        'Please wait while meal packages are loading.';
      return;
    }

    if (this.mealPackagesError) {
      this.bookingError =
        'Unable to load meal packages. Please try again.';
      return;
    }

    const request: BookingRequest = {
      hotelId: this.hotel.hotelId,
      guestName: this.guestName.trim(),
      email: this.email.trim(),
      checkIn: this.checkIn,
      checkOut: this.checkOut,
      mealPackageId: this.selectedMealPackageId ?? undefined,
      rooms: this.rooms.map(room => ({
        roomNumber: room.roomNumber,
        adultsCount: room.adultsCount,
        children5To11Count: room.children5To11Count,
        childrenUnder5Count: room.childrenUnder5Count
      }))
    };

    this.bookingLoading = true;

    this.bookingService.createBooking(request).subscribe({

      next: (response: BookingResponse) => {

        this.bookingSuccess = response;
        this.bookingLoading = false;

        console.log('Booking confirmed:', response);

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

    this.rooms = [
      {
        roomNumber: 1,
        adultsCount: 1,
        children5To11Count: 0,
        childrenUnder5Count: 0
      }
    ];

    this.selectedMealPackageId = null;

    this.bookingError = '';

    this.availableRooms = null;
    this.availabilityError = '';

    this.changeDetectorRef.markForCheck();
  }
}
