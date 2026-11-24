
export interface Hotel {
  hotelId: number;
  name: string;
  location: string;
  description: string | null;
  imageUrl: string | null;

  roomType: string | null;
  totalRooms: number | null;
  pricePerNight: number | null;

  amenities: string[];

  highlights: string | null;
  nearbyAttractions: string | null;
  idealFor: string | null;

  maxAdultsPerRoom: number | null;
  maxChildrenPerRoom: number | null;
  maxOccupancy: number | null;
}
