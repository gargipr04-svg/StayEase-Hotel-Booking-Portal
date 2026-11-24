
import {
  Component,
  OnInit,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { HotelService } from '../../services/hotel.service';
import { Hotel } from '../../models/hotel.model';

import {
  SmartStayMatchRequest,
  SmartStayMatchResponse
} from '../../models/smart-stay-match.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  // Hotel information
  hotels: Hotel[] = [];

  // Loading and error states
  isLoading: boolean = true;
  errorMessage: string = '';
  searchError: string = '';

  // Hotel search fields
  searchLocation: string = '';
  searchCheckIn: string = '';
  searchCheckOut: string = '';

  // Hotel search state
  hasSearched: boolean = false;
  isSearching: boolean = false;

  // Today's date
  today: string = this.getTodayDate();

  // Smart Stay Match properties
  smartBudget: number | null = null;
  smartLocation: string = '';
  smartCheckIn: string = '';
  smartCheckOut: string = '';

  // Available amenities
  availableAmenities: string[] = [
    'AC',
    'Beach Access',
    'Conference Room',
    'Gym',
    'Mountain View',
    'Parking',
    'Restaurant',
    'Swimming Pool',
    'Wi-Fi'
  ];

  selectedAmenities: string[] = [];

  // Smart Stay Match results
  smartMatchResults: SmartStayMatchResponse[] = [];
  smartMatchLoading: boolean = false;
  smartMatchError: string = '';
  hasSmartMatchSearched: boolean = false;

  constructor(
    private readonly hotelService: HotelService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadHotels();
  }

  // Get today's date in YYYY-MM-DD format
  getTodayDate(): string {
    const date = new Date();

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  // Load all hotels
  loadHotels(): void {

    this.isLoading = true;
    this.errorMessage = '';
    this.searchError = '';

    this.hotelService.getAllHotels().subscribe({

      next: (data: Hotel[]) => {
        this.hotels = data;
        this.isLoading = false;

        console.log('Hotels loaded successfully:', this.hotels);

        this.changeDetectorRef.markForCheck();
      },

      error: (error: unknown) => {
        console.error('Error loading hotels:', error);

        this.errorMessage =
          'Unable to load hotels. Please check your connection and try again.';

        this.isLoading = false;

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Search hotels using location and travel dates
  searchHotels(): void {

    this.searchError = '';
    this.errorMessage = '';

    if (!this.searchCheckIn || !this.searchCheckOut) {
      this.searchError =
        'Please select both check-in and check-out dates.';
      return;
    }

    if (this.searchCheckIn < this.today) {
      this.searchError =
        'Check-in date cannot be in the past.';
      return;
    }

    if (this.searchCheckOut <= this.searchCheckIn) {
      this.searchError =
        'Check-out date must be after check-in date.';
      return;
    }

    this.isLoading = true;
    this.isSearching = true;
    this.hasSearched = true;

    this.hotelService.searchHotels(
      this.searchLocation,
      this.searchCheckIn,
      this.searchCheckOut
    ).subscribe({

      next: (data: Hotel[]) => {
        this.hotels = data;
        this.isLoading = false;
        this.isSearching = false;

        console.log('Search results:', this.hotels);

        this.changeDetectorRef.markForCheck();
      },

      error: (error: unknown) => {
        console.error('Hotel search failed:', error);

        this.searchError =
          'Unable to search hotels. Please try again.';

        this.isLoading = false;
        this.isSearching = false;

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Clear search and display all hotels again
  clearSearch(): void {

    this.searchLocation = '';
    this.searchCheckIn = '';
    this.searchCheckOut = '';

    this.hasSearched = false;
    this.isSearching = false;
    this.searchError = '';
    this.errorMessage = '';

    this.loadHotels();
  }

  // Select or deselect an amenity
  toggleAmenity(amenity: string, event: Event): void {

    const checked = (event.target as HTMLInputElement).checked;

    if (checked) {

      if (!this.selectedAmenities.includes(amenity)) {
        this.selectedAmenities.push(amenity);
      }

    } else {

      this.selectedAmenities =
        this.selectedAmenities.filter(item => item !== amenity);

    }
  }

  // Find personalized hotel recommendations
  findSmartMatches(): void {

    this.smartMatchError = '';
    this.smartMatchResults = [];

    if (this.smartBudget === null || this.smartBudget <= 0) {
      this.smartMatchError = 'Please enter a valid budget.';
      return;
    }

    if (!this.smartLocation.trim()) {
      this.smartMatchError = 'Please enter your preferred location.';
      return;
    }

    if (!this.smartCheckIn || !this.smartCheckOut) {
      this.smartMatchError = 'Please select both travel dates.';
      return;
    }

    if (this.smartCheckIn < this.today) {
      this.smartMatchError =
        'Check-in date cannot be in the past.';
      return;
    }

    if (this.smartCheckOut <= this.smartCheckIn) {
      this.smartMatchError =
        'Check-out date must be after check-in date.';
      return;
    }

    const request: SmartStayMatchRequest = {
      budget: this.smartBudget,
      location: this.smartLocation.trim(),
      amenities: this.selectedAmenities,
      checkIn: this.smartCheckIn,
      checkOut: this.smartCheckOut
    };

    this.smartMatchLoading = true;
    this.hasSmartMatchSearched = true;

    this.hotelService.smartStayMatch(request).subscribe({

      next: (data: SmartStayMatchResponse[]) => {

        this.smartMatchResults = data;
        this.smartMatchLoading = false;

        console.log('Smart Stay Match results:', this.smartMatchResults);

        this.changeDetectorRef.markForCheck();
      },

      error: (error: unknown) => {

        console.error('Smart Stay Match failed:', error);

        this.smartMatchError =
          'Unable to find recommendations. Please try again.';

        this.smartMatchLoading = false;

        this.changeDetectorRef.markForCheck();
      }

    });
  }

}
