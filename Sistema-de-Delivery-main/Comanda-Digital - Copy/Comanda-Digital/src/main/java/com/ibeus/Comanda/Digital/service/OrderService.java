package com.ibeus.Comanda.Digital.service;

import com.ibeus.Comanda.Digital.model.*;
import com.ibeus.Comanda.Digital.repository.OrderRepository;
import com.ibeus.Comanda.Digital.repository.KitchenOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;
    
    @Autowired
    private KitchenService kitchenService;
    
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
    
    public List<Order> findActiveOrders() {
        return orderRepository.findActiveOrders();
    }
    
    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public List<Order> findByTableNumber(Integer tableNumber) {
        return orderRepository.findByTableNumber(tableNumber);
    }
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Order create(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Definir tableNumber como 0 se for null (para pedidos de delivery)
        if (order.getTableNumber() == null) {
            order.setTableNumber(0);
        }
        
        // Calcular totalPrice para cada item antes de calcular o total do pedido
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                // Definir unitPrice a partir do dish.price se não estiver definido
                if (item.getUnitPrice() == null && item.getDish() != null) {
                    item.setUnitPrice(item.getDish().getPrice());
                }
                // Definir dishName se não estiver definido
                if (item.getDishName() == null && item.getDish() != null) {
                    item.setDishName(item.getDish().getName());
                }
                // Calcular totalPrice se não estiver definido
                if (item.getTotalPrice() == null) {
                    item.calculateTotal();
                }
            }
        }
        
        order.calculateTotal();
        
        Order savedOrder = orderRepository.save(order);
        
        // Criar pedido na cozinha automaticamente
        createKitchenOrder(savedOrder);
        
        return savedOrder;
    }
    
    public Order update(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setCustomerName(orderDetails.getCustomerName());
        order.setCustomerPhone(orderDetails.getCustomerPhone());
        order.setTableNumber(orderDetails.getTableNumber());
        order.setDeliveryAddress(orderDetails.getDeliveryAddress());
        order.setNotes(orderDetails.getNotes());
        order.setDeliveryFee(orderDetails.getDeliveryFee());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Atualizar itens do pedido
        if (orderDetails.getItems() != null) {
            // Limpar itens existentes
            order.getItems().clear();
            // Adicionar novos itens
            for (OrderItem item : orderDetails.getItems()) {
                // Definir unitPrice a partir do dish.price se não estiver definido
                if (item.getUnitPrice() == null && item.getDish() != null) {
                    item.setUnitPrice(item.getDish().getPrice());
                }
                // Definir dishName se não estiver definido
                if (item.getDishName() == null && item.getDish() != null) {
                    item.setDishName(item.getDish().getName());
                }
                // Calcular totalPrice se não estiver definido
                if (item.getTotalPrice() == null) {
                    item.calculateTotal();
                }
                order.addItem(item);
            }
        }
        
        order.calculateTotal();
        
        return orderRepository.save(order);
    }
    
    public Order updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Sincronizar com a cozinha
        updateKitchenOrderStatus(savedOrder);
        
        return savedOrder;
    }
    
    public Order addItem(Long orderId, OrderItem item) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.addItem(item);
        Order savedOrder = orderRepository.save(order);
        
        // Atualizar pedido na cozinha
        updateKitchenOrder(savedOrder);
        
        return savedOrder;
    }
    
    public Order removeItem(Long orderId, Long itemId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.getItems().removeIf(item -> item.getId().equals(itemId));
        order.calculateTotal();
        
        Order savedOrder = orderRepository.save(order);
        
        // Atualizar pedido na cozinha
        updateKitchenOrder(savedOrder);
        
        return savedOrder;
    }
    
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Cancelar pedido na cozinha
        cancelKitchenOrder(order.getId());
        
        orderRepository.delete(order);
    }
    
    // Métodos privados para integração com a cozinha
    
    private void createKitchenOrder(Order order) {
        KitchenOrder kitchenOrder = new KitchenOrder(
            order.getId(),
            order.getTableNumber(),
            order.getCustomerName()
        );
        
        kitchenOrder.setNotes(order.getNotes());
        kitchenOrder.setTotalAmount(order.getTotalAmount());
        
        // Converter itens do pedido para itens da cozinha
        for (OrderItem orderItem : order.getItems()) {
            KitchenOrderItem kitchenItem = new KitchenOrderItem(orderItem);
            kitchenOrder.addItem(kitchenItem);
        }
        
        kitchenService.createKitchenOrder(kitchenOrder);
    }
    
    private void updateKitchenOrder(Order order) {
        List<KitchenOrder> existingKitchenOrders = kitchenOrderRepository.findByOrderId(order.getId());
        
        if (!existingKitchenOrders.isEmpty()) {
            KitchenOrder kitchenOrder = existingKitchenOrders.get(0);
            
            // Limpar itens existentes
            kitchenOrder.getItems().clear();
            
            // Adicionar novos itens
            for (OrderItem orderItem : order.getItems()) {
                KitchenOrderItem kitchenItem = new KitchenOrderItem(orderItem);
                kitchenOrder.addItem(kitchenItem);
            }
            
            kitchenOrder.setTotalAmount(order.getTotalAmount());
            kitchenOrder.setNotes(order.getNotes());
            kitchenOrder.setUpdatedAt(LocalDateTime.now());
            
            kitchenService.updateKitchenOrder(kitchenOrder);
        }
    }
    
    private void updateKitchenOrderStatus(Order order) {
        List<KitchenOrder> kitchenOrders = kitchenOrderRepository.findByOrderId(order.getId());
        if (!kitchenOrders.isEmpty()) {
            KitchenOrder kitchenOrder = kitchenOrders.get(0);
            
            // Mapear status do pedido para status da cozinha
            KitchenOrder.KitchenOrderStatus kitchenStatus;
            switch (order.getStatus()) {
                case NEW:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.NEW;
                    break;
                case PREPARING:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.PREPARING;
                    break;
                case READY:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.READY;
                    break;
                case DELIVERED:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.SERVED;
                    break;
                case CANCELLED:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.CANCELLED;
                    break;
                default:
                    kitchenStatus = KitchenOrder.KitchenOrderStatus.NEW;
                    break;
            }
            
            kitchenOrder.updateStatus(kitchenStatus);
            kitchenService.updateKitchenOrder(kitchenOrder);
        }
    }
    
    private void cancelKitchenOrder(Long orderId) {
        List<KitchenOrder> kitchenOrders = kitchenOrderRepository.findByOrderId(orderId);
        if (!kitchenOrders.isEmpty()) {
            KitchenOrder kitchenOrder = kitchenOrders.get(0);
            kitchenOrder.updateStatus(KitchenOrder.KitchenOrderStatus.CANCELLED);
            kitchenService.updateKitchenOrder(kitchenOrder);
        }
    }
}
