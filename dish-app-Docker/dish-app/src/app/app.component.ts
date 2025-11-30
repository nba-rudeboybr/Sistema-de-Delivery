import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SimpleOrdersComponent } from './components/simple-orders/simple-orders.component';
import { DishManagementComponent } from './components/dish-management/dish-management.component';
import { KitchenDashboardComponent } from './components/kitchen-dashboard/kitchen-dashboard.component';
import { PaymentManagementComponent } from './components/payment-management/payment-management.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, SimpleOrdersComponent, DishManagementComponent, KitchenDashboardComponent, PaymentManagementComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'dish-app';
  currentTab = 'orders';
}
