import {
  Component,
  ChangeDetectorRef
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { BookingService } from '../../services/booking.service';
import { BookingResponse } from '../../models/booking.model';

@Component({
  selector: 'app-my-bookings',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './my-bookings.component.html',
  styleUrl: './my-bookings.component.css'
})
export class MyBookingsComponent {

  // Customer search details
  bookingReference = '';
  email = '';

  // Retrieved booking
  booking: BookingResponse | null = null;

  // Loading states
  isLoading = false;
  isCancelling = false;

  // Messages
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly bookingService: BookingService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  // Retrieve booking using reference and email
  findBooking(): void {

    this.errorMessage = '';
    this.successMessage = '';
    this.booking = null;

    const reference = this.bookingReference.trim();
    const customerEmail = this.email.trim();

    if (!reference || !customerEmail) {
      this.errorMessage =
        'Please enter your booking reference and email address.';
      return;
    }

    this.isLoading = true;

    this.bookingService.getBooking(
      reference,
      customerEmail
    ).subscribe({

      next: (response: BookingResponse) => {

        this.booking = response;
        this.isLoading = false;

        console.log('Booking retrieved:', response);

        this.changeDetectorRef.markForCheck();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Booking retrieval failed:', error);

        this.booking = null;
        this.isLoading = false;

        this.errorMessage = this.getErrorMessage(
          error,
          'Booking not found. Please check your reference and email.'
        );

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Cancel a confirmed booking
  cancelBooking(): void {

    if (!this.booking) {
      return;
    }

    if (this.booking.status !== 'CONFIRMED') {
      this.errorMessage = 'This booking cannot be cancelled.';
      return;
    }

    const confirmed = window.confirm(
      'Are you sure you want to cancel this reservation?'
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isCancelling = true;

    this.bookingService.cancelBooking(
      this.booking.bookingReference,
      this.email.trim()
    ).subscribe({

      next: (response: BookingResponse) => {

        this.booking = response;
        this.isCancelling = false;

        this.successMessage =
          'Your booking has been cancelled successfully.';

        console.log('Booking cancelled:', response);

        this.changeDetectorRef.markForCheck();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Cancellation failed:', error);

        this.isCancelling = false;

        this.errorMessage = this.getErrorMessage(
          error,
          'Unable to cancel your booking. Please try again.'
        );

        this.changeDetectorRef.markForCheck();
      }

    });
  }

  // Extract a useful message from the backend response
  private getErrorMessage(
    error: HttpErrorResponse,
    fallback: string
  ): string {

    const message = error.error?.message;

    return typeof message === 'string'
      ? message
      : fallback;
  }

  // Clear the search and booking details
  resetSearch(): void {

    this.bookingReference = '';
    this.email = '';

    this.booking = null;

    this.errorMessage = '';
    this.successMessage = '';

    this.isLoading = false;
    this.isCancelling = false;

    this.changeDetectorRef.markForCheck();
  }
}
