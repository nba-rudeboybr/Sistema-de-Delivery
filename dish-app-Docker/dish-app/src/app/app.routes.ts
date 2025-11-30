import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/orders', pathMatch: 'full' },
  { path: 'orders', loadComponent: () => import('./components/simple-orders/simple-orders.component').then(m => m.SimpleOrdersComponent) },
  { path: 'kitchen', loadComponent: () => import('./components/simple-orders/simple-orders.component').then(m => m.SimpleOrdersComponent) },
  { path: 'dishes', loadComponent: () => import('./components/simple-orders/simple-orders.component').then(m => m.SimpleOrdersComponent) }
];
