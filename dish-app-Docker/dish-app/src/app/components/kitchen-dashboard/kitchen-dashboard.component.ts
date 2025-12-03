// src/app/components/kitchen-dashboard/kitchen-dashboard.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-kitchen-dashboard',
  templateUrl: './kitchen-dashboard.component.html',
  styleUrls: ['./kitchen-dashboard.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class KitchenDashboardComponent implements OnInit {
  orders: any[] = [];
  dishes: any[] = [];
  filteredOrders: any[] = [];
  selectedStatus = 'all';
  
  // Contadores para o resumo
  newOrdersCount = 0;
  preparingOrdersCount = 0;
  readyOrdersCount = 0;
  
  statusOptions = [
    { value: 'all', label: 'Todos os Pedidos', icon: '📋' },
    { value: 'NEW', label: 'Novos Pedidos', icon: '🆕' },
    { value: 'PREPARING', label: 'Preparando', icon: '👨‍🍳' },
    { value: 'READY', label: 'Prontos', icon: '✅' }
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadDishes();
  }

  loadOrders(): void {
    this.http.get<any[]>('http://localhost:8080/kitchen/orders').subscribe(orders => {
      this.orders = orders;
      this.updateCounters();
      this.filterOrders();
    });
  }

  loadDishes(): void {
    this.http.get<any[]>('http://localhost:8080/dishes').subscribe(dishes => {
      this.dishes = dishes;
    });
  }

  filterOrders(): void {
    if (this.selectedStatus === 'all') {
      this.filteredOrders = this.orders.filter(order => 
        order.status === 'NEW' || order.status === 'PREPARING' || order.status === 'READY'
      );
    } else {
      this.filteredOrders = this.orders.filter(order => order.status === this.selectedStatus);
    }
    
    // Ordenar por data de criação (mais recentes primeiro)
    this.filteredOrders.sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime());
  }

  onStatusChange(): void {
    this.filterOrders();
  }

  updateCounters(): void {
    this.newOrdersCount = this.orders.filter(o => o.status === 'NEW').length;
    this.preparingOrdersCount = this.orders.filter(o => o.status === 'PREPARING').length;
    this.readyOrdersCount = this.orders.filter(o => o.status === 'READY').length;
  }

  updateOrderStatus(order: any, newStatus: string): void {
    this.http.patch(`http://localhost:8080/kitchen/orders/${order.id}/status`, { status: newStatus }).subscribe((updatedOrder: any) => {
      // Atualizar o pedido na lista local
      const index = this.orders.findIndex(o => o.id === order.id);
      if (index !== -1) {
        this.orders[index] = updatedOrder;
      }
      this.updateCounters();
      this.filterOrders();
      alert(`Status do pedido atualizado para: ${this.getStatusLabel(newStatus)}`);
    }, error => {
      console.error('Erro ao atualizar status do pedido:', error);
      alert('Erro ao atualizar status do pedido. Tente novamente.');
    });
  }

  getStatusIcon(status: string): string {
    const statusMap: { [key: string]: string } = {
      'NEW': '🆕',
      'PREPARING': '👨‍🍳',
      'READY': '✅',
      'DELIVERED': '🚚',
      'PAID': '💰',
      'CANCELLED': '❌'
    };
    return statusMap[status] || '❓';
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'NEW': 'Novo Pedido',
      'PREPARING': 'Preparando',
      'READY': 'Pronto',
      'DELIVERED': 'Entregue',
      'PAID': 'Pago',
      'CANCELLED': 'Cancelado'
    };
    return statusMap[status] || 'Desconhecido';
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'NEW': 'status-new',
      'PREPARING': 'status-preparing',
      'READY': 'status-ready',
      'DELIVERED': 'status-delivered',
      'PAID': 'status-paid',
      'CANCELLED': 'status-cancelled'
    };
    return statusMap[status] || 'status-unknown';
  }

  getDishName(dishId: number): string {
    const dish = this.dishes.find(d => d.id === dishId);
    return dish ? dish.name : 'Prato não encontrado';
  }

  getDishPrice(dishId: number): number {
    const dish = this.dishes.find(d => d.id === dishId);
    return dish ? dish.price : 0;
  }

  calculateOrderTotal(order: any): number {
    if (!order.items || order.items.length === 0) {
      return order.totalAmount || 0;
    }
    
    const subtotal = order.items.reduce((total: number, item: any) => {
      // Verificar se o item tem dish.id ou dishId
      const dishId = item.dish ? item.dish.id : item.dishId;
      return total + (this.getDishPrice(dishId) * item.quantity);
    }, 0);
    
    return subtotal + (order.deliveryFee || 5.00);
  }

  getNextStatus(currentStatus: string): string | null {
    const statusFlow: { [key: string]: string } = {
      'NEW': 'PREPARING',
      'PREPARING': 'READY',
      'READY': 'DELIVERED'
    };
    return statusFlow[currentStatus] || null;
  }

  canUpdateStatus(currentStatus: string): boolean {
    return ['NEW', 'PREPARING', 'READY'].includes(currentStatus);
  }

  deleteOrder(order: any): void {
    if (confirm(`Tem certeza que deseja remover o pedido de ${order.customerName} do histórico?\n\nEsta ação não pode ser desfeita.`)) {
      this.http.delete(`http://localhost:8080/orders/${order.id}`).subscribe(() => {
        this.loadOrders();
        alert('Pedido removido do histórico com sucesso!');
      }, error => {
        console.error('Erro ao remover pedido:', error);
        alert('Erro ao remover pedido. Tente novamente.');
      });
    }
  }

  canDeleteOrder(order: any): boolean {
    // Permite remover pedidos que já foram finalizados
    return order.status === 'DELIVERED' || order.status === 'PAID' || order.status === 'CANCELLED';
  }

  markAsReady(order: any): void {
    if (confirm(`Marcar pedido de ${order.customerName} como PRONTO?\n\nIsso irá sincronizar automaticamente com o sistema de pagamentos.`)) {
      this.http.patch(`http://localhost:8080/kitchen/orders/${order.id}/ready`, {}).subscribe((updatedOrder: any) => {
        // Atualizar o pedido na lista local
        const index = this.orders.findIndex(o => o.id === order.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
        this.updateCounters();
        this.filterOrders();
        alert('Pedido marcado como PRONTO! Agora aparecerá na aba de pagamentos.');
      }, error => {
        console.error('Erro ao marcar como pronto:', error);
        alert('Erro ao marcar como pronto. Tente novamente.');
      });
    }
  }

  markAsDelivered(order: any): void {
    if (confirm(`Marcar pedido de ${order.customerName} como ENTREGUE?\n\nIsso irá sincronizar automaticamente com o sistema de pagamentos.`)) {
      this.http.patch(`http://localhost:8080/kitchen/orders/${order.id}/delivered`, {}).subscribe((updatedOrder: any) => {
        // Atualizar o pedido na lista local
        const index = this.orders.findIndex(o => o.id === order.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
        this.updateCounters();
        this.filterOrders();
        alert('Pedido marcado como ENTREGUE! Agora aparecerá na aba de pagamentos.');
      }, error => {
        console.error('Erro ao marcar como entregue:', error);
        alert('Erro ao marcar como entregue. Tente novamente.');
      });
    }
  }

  markAllItemsAsReady(order: any): void {
    if (confirm(`Marcar TODOS os itens do pedido de ${order.customerName} como prontos?\n\nIsso irá automaticamente marcar o pedido como PRONTO e sincronizar com o sistema de pagamentos.`)) {
      this.http.patch(`http://localhost:8080/kitchen/orders/${order.id}/all-items-ready`, {}).subscribe((updatedOrder: any) => {
        // Atualizar o pedido na lista local
        const index = this.orders.findIndex(o => o.id === order.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
        this.updateCounters();
        this.filterOrders();
        alert('Todos os itens marcados como prontos! Pedido agora está PRONTO e aparecerá na aba de pagamentos.');
      }, error => {
        console.error('Erro ao marcar todos os itens como prontos:', error);
        alert('Erro ao marcar todos os itens como prontos. Tente novamente.');
      });
    }
  }
}