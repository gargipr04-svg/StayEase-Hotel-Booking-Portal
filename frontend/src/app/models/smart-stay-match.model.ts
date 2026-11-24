import { Hotel } from './hotel.model';

export interface SmartStayMatchRequest {
  budget: number;
  location: string;
  amenities: string[];
  checkIn: string;
  checkOut: string;
}

export interface SmartStayMatchResponse {
  hotel: Hotel;
  matchScore: number;
  budgetScore: number;
  locationScore: number;
  amenitiesScore: number;
  availableRooms: number;
  reasons: string[];
}
