// src/app/components/order-management/order-management.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService, Order, OrderItem } from '../../services/order.service';
import { DishService, Dish } from '../../services/dish.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-order-management',
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule]
})
export class OrderManagementComponent implements OnInit {
  orders: Order[] = [];
  dishes: Dish[] = [];
  selectedOrder: Order | null = null;
  newOrder: Order = {
    tableNumber: 0,
    customerName: '',
    status: 'OPEN',
    items: [],
    totalAmount: 0
  };
  showNewOrderForm = false;

  constructor(
    private orderService: OrderService,
    private dishService: DishService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadDishes();
  }

  loadOrders(): void {
    this.orderService.getOrders().subscribe(orders => {
      this.orders = orders;
    });
  }

  loadDishes(): void {
    this.dishService.getDishes().subscribe(dishes => {
      this.dishes = dishes;
    });
  }

  selectOrder(order: Order): void {
    this.selectedOrder = order;
  }

  createNewOrder(): void {
    this.showNewOrderForm = true;
    this.newOrder = {
      tableNumber: 0,
      customerName: '',
      status: 'OPEN',
      items: [],
      totalAmount: 0
    };
  }

  saveNewOrder(): void {
    if (this.newOrder.tableNumber > 0) {
      this.orderService.createOrder(this.newOrder).subscribe(() => {
        this.loadOrders();
        this.showNewOrderForm = false;
        this.newOrder = {
          tableNumber: 0,
          customerName: '',
          status: 'OPEN',
          items: [],
          totalAmount: 0
        };
      });
    }
  }

  addDishToOrder(dish: Dish): void {
    if (this.selectedOrder) {
      const existingItem = this.selectedOrder.items.find(item => item.dishId === dish.id);
      
      if (existingItem) {
        existingItem.quantity += 1;
        existingItem.totalPrice = existingItem.quantity * existingItem.unitPrice;
      } else {
        const newItem: OrderItem = {
          dishId: dish.id!,
          dishName: dish.name,
          quantity: 1,
          unitPrice: dish.price,
          totalPrice: dish.price
        };
        this.selectedOrder.items.push(newItem);
      }
      
      this.calculateTotal();
    }
  }

  removeItemFromOrder(item: OrderItem): void {
    if (this.selectedOrder) {
      this.selectedOrder.items = this.selectedOrder.items.filter(i => i !== item);
      this.calculateTotal();
    }
  }

  updateItemQuantity(item: OrderItem, quantity: number): void {
    if (quantity > 0) {
      item.quantity = quantity;
      item.totalPrice = item.quantity * item.unitPrice;
      this.calculateTotal();
    }
  }

  calculateTotal(): void {
    if (this.selectedOrder) {
      this.selectedOrder.totalAmount = this.selectedOrder.items.reduce(
        (total, item) => total + item.totalPrice, 0
      );
    }
  }

  saveOrder(): void {
    if (this.selectedOrder) {
      this.orderService.updateOrder(this.selectedOrder.id!, this.selectedOrder).subscribe(() => {
        this.loadOrders();
      });
    }
  }

  updateOrderStatus(order: Order, status: string): void {
    this.orderService.updateOrderStatus(order.id!, status).subscribe(() => {
      order.status = status as any;
      this.loadOrders();
    });
  }

  deleteOrder(order: Order): void {
    if (confirm('Tem certeza que deseja excluir esta comanda?')) {
      this.orderService.deleteOrder(order.id!).subscribe(() => {
        this.loadOrders();
        if (this.selectedOrder?.id === order.id) {
          this.selectedOrder = null;
        }
      });
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'OPEN': return '#28a745';
      case 'IN_PROGRESS': return '#ffc107';
      case 'COMPLETED': return '#17a2b8';
      case 'CANCELLED': return '#dc3545';
      default: return '#6c757d';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'OPEN': return 'Aberta';
      case 'IN_PROGRESS': return 'Em Andamento';
      case 'COMPLETED': return 'Finalizada';
      case 'CANCELLED': return 'Cancelada';
      default: return status;
    }
  }
}

