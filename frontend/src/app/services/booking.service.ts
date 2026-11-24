import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  BookingRequest,
  BookingResponse
} from '../models/booking.model';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private readonly apiUrl = 'http://localhost:8080/api/bookings';

  constructor(private http: HttpClient) {}

  // 1. Create a new booking
  createBooking(
    request: BookingRequest
  ): Observable<BookingResponse> {

    return this.http.post<BookingResponse>(
      this.apiUrl,
      request
    );
  }

  // 2. Retrieve a booking using reference and email
  getBooking(
    bookingReference: string,
    email: string
  ): Observable<BookingResponse> {

    const params = new HttpParams()
      .set('email', email.trim());

    return this.http.get<BookingResponse>(
      `${this.apiUrl}/${encodeURIComponent(bookingReference.trim())}`,
      { params }
    );
  }

  // 3. Cancel an existing booking
  cancelBooking(
    bookingReference: string,
    email: string
  ): Observable<BookingResponse> {

    const url =
      `${this.apiUrl}/${encodeURIComponent(bookingReference.trim())}/cancel`;

    return this.http.patch<BookingResponse>(
      url,
      { email: email.trim() }
    );
  }

}
