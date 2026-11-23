import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

import { HotelService } from './services/hotel.service';
import { Hotel } from './models/hotel.model';

@Component({
  selector: 'app-root',
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class App implements OnInit {

  // Stores hotel information received from Spring Boot
  hotels: Hotel[] = [];

  // Controls the loading indicator
  isLoading: boolean = true;

  // Stores any error message
  errorMessage: string = '';

  // Constructor injection
  constructor(private hotelService: HotelService) {}

  // Runs automatically when the component loads
  ngOnInit(): void {
    this.loadHotels();
  }

  // Fetch hotels from the backend
  loadHotels(): void {

    this.isLoading = true;
    this.errorMessage = '';

    this.hotelService.getAllHotels().subscribe({

      next: (data: Hotel[]) => {
        this.hotels = data;
        this.isLoading = false;

        console.log('Hotels loaded successfully:', this.hotels);
      },

      error: (error) => {
        console.error('Error loading hotels:', error);

        this.errorMessage =
          'Unable to load hotels. Please check your connection and try again.';

        this.isLoading = false;
      }

    });
  }
}
