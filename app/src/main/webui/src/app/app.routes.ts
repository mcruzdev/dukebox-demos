import { Routes } from '@angular/router';
import { OrderStatus } from './pages/order-status/order-status';
import { Store } from './pages/store/store';

export const routes: Routes = [
  {
    path: '', component: Store
  },
  {
    path: 'order-status/:id', component: OrderStatus
  }
];
