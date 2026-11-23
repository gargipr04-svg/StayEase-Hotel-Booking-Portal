import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Hotel } from '../models/hotel.model';

@Injectable({
  providedIn: 'root'
})
export class HotelService {

  // Spring Boot hotel API URL
  private readonly apiUrl = 'http://localhost:8080/api/hotels';

  constructor(private http: HttpClient) {}

  // Retrieve all hotels
  getAllHotels(): Observable<Hotel[]> {
    return this.http.get<Hotel[]>(this.apiUrl);
  }

  // Retrieve one hotel by ID
  getHotelById(hotelId: number): Observable<Hotel> {
    return this.http.get<Hotel>(`${this.apiUrl}/${hotelId}`);
  }

  // Search hotels by location and dates
  searchHotels(
    location: string,
    checkIn: string,
    checkOut: string
  ): Observable<Hotel[]> {

    let params = new HttpParams()
      .set('checkIn', checkIn)
      .set('checkOut', checkOut);

    if (location.trim()) {
      params = params.set('location', location.trim());
    }

    return this.http.get<Hotel[]>(
      `${this.apiUrl}/search`,
      { params }
    );
  }
}
