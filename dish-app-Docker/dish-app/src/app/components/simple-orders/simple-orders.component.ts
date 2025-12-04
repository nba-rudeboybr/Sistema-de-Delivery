// src/app/components/simple-orders/simple-orders.component.ts

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { OrderStateService } from '../../services/order-state.service';

@Component({
  selector: 'app-simple-orders',
  templateUrl: './simple-orders.component.html',
  styleUrls: ['./simple-orders.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule]
})
export class SimpleOrdersComponent implements OnInit {
  orders: any[] = [];
  dishes: any[] = [];
  selectedOrder: any = null;
  newOrder = {
    customerName: '',
    customerPhone: '',
    deliveryAddress: '',
    status: 'NEW',
    items: [],
    totalAmount: 0,
    deliveryFee: 5.00
  };
  showNewOrderForm = false;

  constructor(private http: HttpClient, private orderStateService: OrderStateService) {}

  ngOnInit(): void {
    console.log('SimpleOrdersComponent: Inicializando componente...');
    
    // Subscrever às mudanças nos pedidos
    this.orderStateService.orders$.subscribe(orders => {
      console.log('SimpleOrdersComponent: Pedidos recebidos:', orders);
      this.orders = orders;
    });

    // Subscrever às mudanças no pedido selecionado
    this.orderStateService.selectedOrder$.subscribe(order => {
      console.log('SimpleOrdersComponent: Pedido selecionado:', order);
      this.selectedOrder = order;
    });

    this.loadDishes();
    this.loadOrders();
  }

  loadDishes(): void {
    console.log('Carregando pratos...');
    this.http.get<any[]>('http://localhost:8080/dishes').subscribe({
      next: (dishes) => {
        console.log('Pratos carregados:', dishes);
        this.dishes = dishes;
      },
      error: (error) => {
        console.error('Erro ao carregar pratos:', error);
        // Criar alguns pratos de exemplo se não conseguir carregar
        this.dishes = [
          { id: 1, name: 'Pizza Margherita', price: 25.90, description: 'Pizza com molho de tomate, mussarela e manjericão' },
          { id: 2, name: 'Hambúrguer Clássico', price: 18.50, description: 'Hambúrguer com carne, queijo, alface e tomate' },
          { id: 3, name: 'Batata Frita', price: 8.90, description: 'Batata frita crocante' },
          { id: 4, name: 'Refrigerante', price: 4.50, description: 'Refrigerante gelado' }
        ];
      }
    });
  }

  loadOrders(): void {
    console.log('Carregando pedidos...');
    this.http.get<any[]>('http://localhost:8080/orders').subscribe({
      next: (orders) => {
        console.log('Pedidos carregados:', orders);
        this.orders = orders;
        
        // Limpar pedido selecionado se não existir mais na lista
        if (this.selectedOrder) {
          const orderExists = orders.find((o: any) => o.id === this.selectedOrder.id);
          if (!orderExists) {
            this.selectedOrder = null;
            this.orderStateService.setSelectedOrder(null);
          }
        }
        
        // Usar o método loadOrders do serviço para atualizar o estado
        this.orderStateService.loadOrders();
      },
      error: (error) => {
        console.error('Erro ao carregar pedidos:', error);
        this.orders = [];
        // Limpar pedido selecionado se houver erro
        if (this.selectedOrder) {
          this.selectedOrder = null;
          this.orderStateService.setSelectedOrder(null);
        }
      }
    });
  }

  selectOrder(order: any): void {
    console.log('Selecionando pedido:', order);
    this.selectedOrder = order;
    this.orderStateService.setSelectedOrder(order);
  }

  addItemToOrder(dish: any): void {
    if (this.selectedOrder) {
      console.log('Adicionando item ao pedido:', dish);
      
      // Criar item para enviar ao backend
      const itemToAdd = {
        dishId: dish.id,
        dishName: dish.name,
        quantity: 1,
        unitPrice: dish.price,
        totalPrice: dish.price
      };
      
      console.log('Enviando item para o backend:', itemToAdd);
      
      // Usar endpoint específico para adicionar item
      this.http.post(`http://localhost:8080/orders/${this.selectedOrder.id}/items`, itemToAdd).subscribe({
        next: (updatedOrder) => {
          console.log('✅ Item adicionado com sucesso:', updatedOrder);
          
          // Atualizar o pedido selecionado
          this.selectedOrder = updatedOrder;
          this.orderStateService.updateOrder(updatedOrder);
          
          // Recarregar lista completa para garantir sincronização
          this.loadOrders();
          
          // Re-selecionar o pedido atualizado
          setTimeout(() => {
            this.selectOrder(updatedOrder);
          }, 100);
          
          alert('✅ Item adicionado com sucesso!');
        },
        error: (error) => {
          console.error('❌ Erro ao adicionar item:', error);
          alert('Erro ao adicionar item. Tente novamente.');
        }
      });
    } else {
      console.log('Nenhum pedido selecionado');
      alert('Selecione um pedido primeiro para adicionar itens.');
    }
  }

  removeItemFromOrder(item: any): void {
    if (this.selectedOrder && item.id) {
      console.log('Removendo item do pedido:', item);
      console.log('Pedido atual:', this.selectedOrder);
      
      // Usar endpoint específico para remover item
      this.http.delete(`http://localhost:8080/orders/${this.selectedOrder.id}/items/${item.id}`).subscribe({
        next: (updatedOrder) => {
          console.log('✅ Item removido com sucesso:', updatedOrder);
          console.log('Pedido atualizado:', updatedOrder);
          
          // Atualizar o pedido selecionado
          this.selectedOrder = updatedOrder;
          this.orderStateService.updateOrder(updatedOrder);
          
          // Recarregar lista completa para garantir sincronização
          this.loadOrders();
          
          // Re-selecionar o pedido atualizado
          setTimeout(() => {
            this.selectOrder(updatedOrder);
            console.log('Pedido re-selecionado:', this.selectedOrder);
          }, 100);
          
          alert('✅ Item removido com sucesso!');
        },
        error: (error) => {
          console.error('❌ Erro ao remover item:', error);
          alert('Erro ao remover item. Tente novamente.');
        }
      });
    } else {
      console.log('Item não tem ID válido ou pedido não selecionado');
      console.log('Item:', item);
      console.log('Pedido selecionado:', this.selectedOrder);
      alert('Erro: Item não pode ser removido.');
    }
  }

  increaseQuantity(item: any): void {
    if (this.selectedOrder && item.id) {
      const newQuantity = item.quantity + 1;
      this.updateItemQuantity(item, newQuantity);
    }
  }

  decreaseQuantity(item: any): void {
    if (this.selectedOrder && item.id) {
      const newQuantity = item.quantity - 1;
      if (newQuantity <= 0) {
        // Se a quantidade for 0 ou menor, remover o item
        this.removeItemFromOrder(item);
      } else {
        this.updateItemQuantity(item, newQuantity);
      }
    }
  }

  updateItemQuantity(item: any, newQuantity: number): void {
    if (this.selectedOrder && item.id) {
      console.log('Atualizando quantidade do item:', item, 'Nova quantidade:', newQuantity);
      
      // Usar endpoint específico para atualizar quantidade
      this.http.patch(`http://localhost:8080/orders/${this.selectedOrder.id}/items/${item.id}`, { quantity: newQuantity }).subscribe({
        next: (updatedOrder) => {
          console.log('✅ Quantidade atualizada com sucesso:', updatedOrder);
          
          // Atualizar o pedido selecionado
          this.selectedOrder = updatedOrder;
          this.orderStateService.updateOrder(updatedOrder);
          
          // Recarregar lista completa para garantir sincronização
          this.loadOrders();
          
          // Re-selecionar o pedido atualizado
          setTimeout(() => {
            this.selectOrder(updatedOrder);
          }, 100);
        },
        error: (error) => {
          console.error('❌ Erro ao atualizar quantidade:', error);
          alert('Erro ao atualizar quantidade. Tente novamente.');
        }
      });
    }
  }

  calculateTotal(): void {
    if (this.selectedOrder) {
      this.selectedOrder.totalAmount = this.selectedOrder.items.reduce((total: number, item: any) => {
        return total + item.totalPrice;
      }, 0);
      console.log('Total calculado:', this.selectedOrder.totalAmount);
    }
  }

  saveNewOrder(): void {
    console.log('Salvando novo pedido:', this.newOrder);
    
    if (!this.newOrder.customerName.trim()) {
      alert('Por favor, preencha o nome do cliente.');
      return;
    }

    // Criar pedido com dados básicos
    const phone = this.newOrder.customerPhone ? this.newOrder.customerPhone.trim() : '';
    const address = this.newOrder.deliveryAddress ? this.newOrder.deliveryAddress.trim() : '';
    
    const orderToSave = {
      tableNumber: Math.floor(Math.random() * 10) + 1,
      customerName: this.newOrder.customerName.trim(),
      customerPhone: phone.length > 0 ? phone : null,
      deliveryAddress: address.length > 0 ? address : null,
      status: 'NEW',
      items: [],
      totalAmount: 0
    };
    
    console.log('=== DEBUG: Dados do pedido ===');
    console.log('customerPhone:', phone, 'length:', phone.length);
    console.log('deliveryAddress:', address, 'length:', address.length);
    console.log('Enviando pedido para o backend:', JSON.stringify(orderToSave, null, 2));
    
    this.http.post('http://localhost:8080/orders', orderToSave).subscribe({
      next: (savedOrder) => {
        console.log('✅ Pedido salvo com sucesso:', savedOrder);
        
        // NÃO enviar automaticamente para a cozinha
        // O usuário deve adicionar itens primeiro e depois enviar manualmente
        
        this.showNewOrderForm = false;
        
        // Limpar formulário
        this.newOrder = {
          customerName: '',
          customerPhone: '',
          deliveryAddress: '',
          status: 'NEW',
          items: [],
          totalAmount: 0,
          deliveryFee: 5.00
        };
        
        // Recarregar lista de pedidos
        this.loadOrders();
        
        // Selecionar o pedido recém-criado para adicionar itens
        this.selectOrder(savedOrder);
        
        alert('✅ Pedido criado! Agora adicione itens e envie para a cozinha quando estiver pronto.');
      },
      error: (error) => {
        console.error('❌ Erro ao salvar pedido:', error);
        alert('Erro ao criar pedido. Tente novamente.');
      }
    });
  }

  sendToKitchen(orderId: number): void {
    console.log('Enviando pedido para a cozinha:', orderId);
    
    // Verificar se o pedido tem itens antes de enviar
    const order = this.orders.find(o => o.id === orderId);
    if (!order || !order.items || order.items.length === 0) {
      alert('⚠️ Adicione pelo menos um item ao pedido antes de enviar para a cozinha!');
      return;
    }
    
    // Atualizar status do pedido para PREPARING (isso automaticamente cria o pedido na cozinha)
    this.http.patch(`http://localhost:8080/orders/${orderId}/status`, { status: 'PREPARING' }).subscribe({
      next: (response) => {
        console.log('✅ Pedido enviado para a cozinha:', response);
        alert('✅ Pedido enviado para a cozinha com sucesso!');
        
        // Recarregar pedidos para mostrar o status atualizado
        this.loadOrders();
      },
      error: (error) => {
        console.error('❌ Erro ao enviar para cozinha:', error);
        alert('⚠️ Erro ao enviar pedido para cozinha. Tente novamente.');
      }
    });
  }

  canSendToKitchen(): boolean {
    const canSend = this.selectedOrder && 
           this.selectedOrder.status === 'NEW' && 
           this.selectedOrder.items && 
           this.selectedOrder.items.length > 0;
    console.log(`Pode enviar para cozinha: ${canSend}`, {
      hasOrder: !!this.selectedOrder,
      status: this.selectedOrder?.status,
      itemsCount: this.selectedOrder?.items?.length || 0
    });
    return canSend;
  }

  canEditOrder(): boolean {
    const canEdit = this.selectedOrder && 
           (this.selectedOrder.status === 'NEW' || this.selectedOrder.status === 'PREPARING');
    console.log(`Pode editar pedido: ${canEdit}`, {
      hasOrder: !!this.selectedOrder,
      status: this.selectedOrder?.status
    });
    return canEdit;
  }

  deleteOrder(order: any): void {
    console.log(`Tentando remover pedido:`, order);
    if (confirm(`Tem certeza que deseja remover o pedido de ${order.customerName}?\n\nEsta ação não pode ser desfeita.`)) {
      console.log(`Confirmado! Removendo pedido ID: ${order.id}`);
      this.http.delete(`http://localhost:8080/orders/${order.id}`).subscribe({
        next: () => {
          console.log(`Pedido ${order.id} removido com sucesso!`);
          
          // Limpar o pedido selecionado se for o pedido que foi deletado
          if (this.selectedOrder && this.selectedOrder.id === order.id) {
            this.selectedOrder = null;
            this.orderStateService.setSelectedOrder(null);
          }
          
          // Recarregar lista de pedidos
          this.loadOrders();
          
          alert('Pedido removido com sucesso!');
        },
        error: (error) => {
          console.error('Erro ao remover pedido:', error);
          alert('Erro ao remover pedido. Tente novamente.');
        }
      });
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'NEW': return 'status-new';
      case 'PREPARING': return 'status-preparing';
      case 'READY': return 'status-ready';
      case 'DELIVERED': return 'status-delivered';
      case 'PAID': return 'status-paid';
      case 'CANCELLED': return 'status-cancelled';
      default: return 'status-default';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'NEW': return 'Novo';
      case 'PREPARING': return 'Preparando';
      case 'READY': return 'Pronto';
      case 'DELIVERED': return 'Entregue';
      case 'PAID': return 'Pago';
      case 'CANCELLED': return 'Cancelado';
      default: return status;
    }
  }
}