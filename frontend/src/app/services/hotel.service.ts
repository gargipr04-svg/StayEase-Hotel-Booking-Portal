import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Hotel } from '../models/hotel.model';

import {
  SmartStayMatchRequest,
  SmartStayMatchResponse
} from '../models/smart-stay-match.model';

// Room availability response from Spring Boot
export interface RoomAvailability {
  hotelId: number;
  checkIn: string;
  checkOut: string;
  available: boolean;
  availableRooms: number;
}

// Meal package response from Spring Boot
export interface MealPackage {
  packageId: number;
  packageName: string;
  includesBreakfast: boolean;
  includesLunch: boolean;
  includesDinner: boolean;
  pricePerAdultPerNight: number;
}

@Injectable({
  providedIn: 'root'
})
export class HotelService {

  // Spring Boot hotel API URL
  private readonly apiUrl = 'http://localhost:8080/api/hotels';

  constructor(private http: HttpClient) {}

  // Smart Stay Match recommendations
  smartStayMatch(
    request: SmartStayMatchRequest
  ): Observable<SmartStayMatchResponse[]> {
    return this.http.post<SmartStayMatchResponse[]>(
      `${this.apiUrl}/smart-match`,
      request
    );
  }

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

  // Retrieve available rooms for selected dates
  getAvailability(
    hotelId: number,
    checkIn: string,
    checkOut: string
  ): Observable<RoomAvailability> {

    const params = new HttpParams()
      .set('checkIn', checkIn)
      .set('checkOut', checkOut);

    return this.http.get<RoomAvailability>(
      `${this.apiUrl}/${hotelId}/availability`,
      { params }
    );
  }

  // Retrieve meal packages for a particular hotel
  getMealPackages(
    hotelId: number
  ): Observable<MealPackage[]> {

    return this.http.get<MealPackage[]>(
      `${this.apiUrl}/${hotelId}/meal-packages`
    );
  }
}
