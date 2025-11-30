// src/app/services/kitchen.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface KitchenOrderItem {
  id?: number;
  dishId: number;
  dishName: string;
  dishDescription?: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  preparationStatus: 'PENDING' | 'IN_PROGRESS' | 'READY' | 'SERVED';
  preparationNotes?: string;
  estimatedPrepTime?: number;
}

export interface KitchenOrder {
  id?: number;
  orderId: number;
  tableNumber: number;
  customerName?: string;
  status: 'NEW' | 'PREPARING' | 'READY' | 'DELIVERED' | 'PAID' | 'CANCELLED';
  items: KitchenOrderItem[];
  totalAmount: number;
  estimatedTime?: number;
  priority: number;
  notes?: string;
  createdAt?: Date;
  updatedAt?: Date;
  startedAt?: Date;
  readyAt?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class KitchenService {
  private apiUrl = 'http://localhost:8080/kitchen';

  constructor(private http: HttpClient) {}

  getActiveKitchenOrders(): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/active`);
  }

  getNewOrders(): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/new`);
  }

  getPreparingOrders(): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/preparing`);
  }

  getReadyOrders(): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/ready`);
  }

  getOrdersByTable(tableNumber: number): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/table/${tableNumber}`);
  }

  getKitchenOrder(id: number): Observable<KitchenOrder> {
    return this.http.get<KitchenOrder>(`${this.apiUrl}/orders/${id}`);
  }

  createKitchenOrder(kitchenOrder: KitchenOrder): Observable<KitchenOrder> {
    return this.http.post<KitchenOrder>(`${this.apiUrl}/orders`, kitchenOrder);
  }

  updateOrderStatus(id: number, status: string): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/status`, { status });
  }

  updateItemPreparationStatus(orderId: number, itemId: number, status: string): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${orderId}/items/${itemId}/status`, { status });
  }

  addPreparationNotes(orderId: number, itemId: number, notes: string): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${orderId}/items/${itemId}/notes`, { notes });
  }

  updateOrderPriority(id: number, priority: number): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/priority`, { priority });
  }

  addOrderNotes(id: number, notes: string): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/notes`, { notes });
  }

  updateEstimatedTime(id: number, estimatedTime: number): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/estimated-time`, { estimatedTime });
  }

  markOrderAsReady(id: number): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/ready`, {});
  }

  markOrderAsDelivered(id: number): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/delivered`, {});
  }

  cancelOrder(id: number): Observable<KitchenOrder> {
    return this.http.patch<KitchenOrder>(`${this.apiUrl}/orders/${id}/cancel`, {});
  }

  getOrdersByPriority(priority: number): Observable<KitchenOrder[]> {
    return this.http.get<KitchenOrder[]>(`${this.apiUrl}/orders/priority/${priority}`);
  }

  getOrderCountByStatus(status: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/orders/count/${status}`);
  }

  deleteKitchenOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/orders/${id}`);
  }

  // Helper methods
  getStatusColor(status: string): string {
    switch (status) {
      case 'NEW': return '#28a745';
      case 'PREPARING': return '#ffc107';
      case 'READY': return '#17a2b8';
      case 'DELIVERED': return '#6c757d';
      case 'PAID': return '#28a745';
      case 'CANCELLED': return '#dc3545';
      default: return '#6c757d';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'NEW': return 'Novo Pedido';
      case 'PREPARING': return 'Preparando';
      case 'READY': return 'Pronto';
      case 'DELIVERED': return 'Entregue';
      case 'PAID': return 'Pago';
      case 'CANCELLED': return 'Cancelado';
      default: return status;
    }
  }

  getPriorityColor(priority: number): string {
    switch (priority) {
      case 1: return '#28a745'; // Normal
      case 2: return '#ffc107'; // Alta
      case 3: return '#dc3545'; // Urgente
      default: return '#6c757d';
    }
  }

  getPriorityText(priority: number): string {
    switch (priority) {
      case 1: return 'Normal';
      case 2: return 'Alta';
      case 3: return 'Urgente';
      default: return 'Normal';
    }
  }
}
