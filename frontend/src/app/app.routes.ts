import { Routes } from '@angular/router';

import { HomeComponent } from './pages/home/home.component';
import { HotelDetailsComponent } from './pages/hotel-details/hotel-details.component';
import { MyBookingsComponent } from './pages/my-bookings/my-bookings.component';

export const routes: Routes = [

  // Homepage
  {
    path: '',
    component: HomeComponent
  },

  // Individual hotel details
  {
    path: 'hotels/:id',
    component: HotelDetailsComponent
  },
  {
  path: 'my-bookings',
  component: MyBookingsComponent
  },

  // Unknown URLs return to homepage
  {
    path: '**',
    redirectTo: ''
  }

];
