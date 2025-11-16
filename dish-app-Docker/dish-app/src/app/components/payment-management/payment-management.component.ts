// src/app/components/payment-management/payment-management.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-payment-management',
  templateUrl: './payment-management.component.html',
  styleUrls: ['./payment-management.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class PaymentManagementComponent implements OnInit {
  orders: any[] = [];
  dishes: any[] = [];
  filteredOrders: any[] = [];
  selectedStatus = 'all';
  
  // Contadores para o resumo
  pendingPaymentsCount = 0;
  paidOrdersCount = 0;
  totalRevenue = 0;
  
  // Contadores originais para reset
  private originalTotalRevenue = 0;
  
  statusOptions = [
    { value: 'all', label: 'Todos os Pedidos', icon: '📋' },
    { value: 'READY', label: 'Prontos para Pagamento', icon: '💰' },
    { value: 'DELIVERED', label: 'Entregues', icon: '🚚' },
    { value: 'PAID', label: 'Pagos', icon: '✅' }
  ];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadDishes();
    this.originalTotalRevenue = this.totalRevenue;
  }

  loadOrders(): void {
    this.http.get<any[]>('http://localhost:8080/orders').subscribe(orders => {
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

  updateCounters(): void {
    this.pendingPaymentsCount = this.orders.filter(o => o.status === 'READY' || o.status === 'DELIVERED').length;
    this.paidOrdersCount = this.orders.filter(o => o.status === 'PAID').length;
    this.totalRevenue = this.orders
      .filter(o => o.status === 'PAID')
      .reduce((total, order) => total + this.calculateOrderTotal(order), 0);
  }

  filterOrders(): void {
    if (this.selectedStatus === 'all') {
      this.filteredOrders = this.orders.filter(order => 
        order.status === 'READY' || order.status === 'DELIVERED'
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

  updateOrderStatus(order: any, newStatus: string): void {
    this.http.patch(`http://localhost:8080/orders/${order.id}/status`, { status: newStatus }).subscribe((updatedOrder: any) => {
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
      'READY': '💰',
      'DELIVERED': '🚚',
      'PAID': '✅',
      'CANCELLED': '❌'
    };
    return statusMap[status] || '❓';
  }

  getStatusLabel(status: string): string {
    const statusMap: { [key: string]: string } = {
      'NEW': 'Novo Pedido',
      'PREPARING': 'Preparando',
      'READY': 'Pronto para Pagamento',
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

  canProcessPayment(order: any): boolean {
    return order.status === 'READY' || order.status === 'DELIVERED';
  }

  processPayment(order: any): void {
    const total = this.calculateOrderTotal(order);
    if (confirm(`Confirmar pagamento do pedido de ${order.customerName} no valor de R$ ${total.toFixed(2)}?\n\nApós a confirmação, o pedido será marcado como PAGO e não aparecerá mais na lista de pedidos pendentes.`)) {
      this.http.patch(`http://localhost:8080/orders/${order.id}/status`, { status: 'PAID' }).subscribe((updatedOrder: any) => {
        // Atualizar o pedido na lista local
        const index = this.orders.findIndex(o => o.id === order.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
        this.updateCounters();
        this.filterOrders();
        alert('Pagamento processado com sucesso!');
      }, error => {
        console.error('Erro ao processar pagamento:', error);
        alert('Erro ao processar pagamento. Tente novamente.');
      });
    }
  }

  markAsDelivered(order: any): void {
    if (confirm(`Marcar pedido de ${order.customerName} como entregue?`)) {
      this.http.patch(`http://localhost:8080/orders/${order.id}/status`, { status: 'DELIVERED' }).subscribe((updatedOrder: any) => {
        // Atualizar o pedido na lista local
        const index = this.orders.findIndex(o => o.id === order.id);
        if (index !== -1) {
          this.orders[index] = updatedOrder;
        }
        this.updateCounters();
        this.filterOrders();
        alert('Pedido marcado como entregue!');
      }, error => {
        console.error('Erro ao marcar como entregue:', error);
        alert('Erro ao marcar como entregue. Tente novamente.');
      });
    }
  }

  resetFinancialSummary(): void {
    if (confirm('Tem certeza que deseja resetar o resumo financeiro?\n\nIsso irá zerar a receita total e todos os contadores. Esta ação não pode ser desfeita.')) {
      this.totalRevenue = 0;
      this.pendingPaymentsCount = 0;
      this.paidOrdersCount = 0;
      this.originalTotalRevenue = 0;
      alert('Resumo financeiro resetado com sucesso!');
    }
  }

  removeOrderFromHistory(order: any): void {
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

  canRemoveFromHistory(order: any): boolean {
    // Só permite remover pedidos que já foram pagos ou cancelados
    return order.status === 'PAID' || order.status === 'CANCELLED';
  }
}
