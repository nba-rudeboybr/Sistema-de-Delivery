// src/app/services/order-state.service.ts

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class OrderStateService {
  private ordersSubject = new BehaviorSubject<any[]>([]);
  public orders$ = this.ordersSubject.asObservable();

  private selectedOrderSubject = new BehaviorSubject<any>(null);
  public selectedOrder$ = this.selectedOrderSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadOrders();
  }

  loadOrders(): void {
    console.log('OrderStateService: Carregando pedidos do backend...');
    this.http.get<any[]>('http://localhost:8080/orders').subscribe({
      next: (orders) => {
        console.log('OrderStateService: Pedidos carregados:', orders);
        this.ordersSubject.next(orders);
      },
      error: (error) => {
        console.error('OrderStateService: Erro ao carregar pedidos:', error);
      }
    });
  }

  getOrders(): any[] {
    const orders = this.ordersSubject.value;
    console.log('OrderStateService: Obtendo pedidos:', orders);
    return orders;
  }

  getSelectedOrder(): any {
    const selectedOrder = this.selectedOrderSubject.value;
    console.log('OrderStateService: Obtendo pedido selecionado:', selectedOrder);
    return selectedOrder;
  }

  setSelectedOrder(order: any): void {
    console.log('OrderStateService: Definindo pedido selecionado:', order);
    this.selectedOrderSubject.next(order);
  }

  updateOrder(updatedOrder: any): void {
    console.log('OrderStateService: Atualizando pedido:', updatedOrder);
    const orders = this.getOrders();
    const index = orders.findIndex(o => o.id === updatedOrder.id);
    if (index !== -1) {
      orders[index] = updatedOrder;
      this.ordersSubject.next(orders);
    }

    // Atualizar pedido selecionado se for o mesmo
    const selectedOrder = this.getSelectedOrder();
    if (selectedOrder && selectedOrder.id === updatedOrder.id) {
      console.log('OrderStateService: Atualizando pedido selecionado');
      this.selectedOrderSubject.next(updatedOrder);
    }
  }

  addOrder(newOrder: any): void {
    console.log('OrderStateService: Adicionando novo pedido:', newOrder);
    const orders = this.getOrders();
    orders.push(newOrder);
    this.ordersSubject.next(orders);
  }

  removeOrder(orderId: number): void {
    console.log(`OrderStateService: Removendo pedido ${orderId} da lista local`);
    const orders = this.getOrders();
    const filteredOrders = orders.filter(o => o.id !== orderId);
    console.log(`OrderStateService: Pedidos antes: ${orders.length}, depois: ${filteredOrders.length}`);
    this.ordersSubject.next(filteredOrders);

    // Limpar pedido selecionado se for o removido
    const selectedOrder = this.getSelectedOrder();
    if (selectedOrder && selectedOrder.id === orderId) {
      console.log(`OrderStateService: Limpando pedido selecionado`);
      this.selectedOrderSubject.next(null);
    }
  }

  updateOrderStatus(orderId: number, status: string): Observable<any> {
    console.log(`OrderStateService: Atualizando status do pedido ${orderId} para ${status}`);
    return this.http.patch(`http://localhost:8080/orders/${orderId}/status`, { status });
  }

  saveOrder(order: any): Observable<any> {
    console.log('OrderStateService: Salvando pedido:', order);
    return this.http.put(`http://localhost:8080/orders/${order.id}`, order);
  }

  createOrder(order: any): Observable<any> {
    console.log('OrderStateService: Criando novo pedido:', order);
    return this.http.post('http://localhost:8080/orders', order);
  }

  deleteOrder(orderId: number): Observable<any> {
    console.log(`OrderStateService: Removendo pedido ID: ${orderId}`);
    return this.http.delete(`http://localhost:8080/orders/${orderId}`);
  }
}