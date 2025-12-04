package com.restaurant.order.service;

import com.restaurant.order.model.Order;
import com.restaurant.order.model.OrderItem;
import com.restaurant.order.model.OrderStatus;
import com.restaurant.order.model.KitchenOrder;
import com.restaurant.order.model.KitchenOrderItem;
import com.restaurant.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private KitchenService kitchenService;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByTable(Integer tableNumber) {
        return orderRepository.findByTableNumber(tableNumber);
    }
    
    public List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }
    
    public Order createOrder(Order order) {
        order.calculateTotal();
        return orderRepository.save(order);
    }
    
    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setTableNumber(orderDetails.getTableNumber());
            order.setCustomerName(orderDetails.getCustomerName());
            order.setCustomerPhone(orderDetails.getCustomerPhone());
            order.setDeliveryAddress(orderDetails.getDeliveryAddress());
            order.setStatus(orderDetails.getStatus());
            order.setItems(orderDetails.getItems());
            order.calculateTotal();
            Order savedOrder = orderRepository.save(order);
            
            // Se o pedido já foi enviado para a cozinha, sincronizar as mudanças
            if (order.getStatus() == OrderStatus.PREPARING) {
                syncOrderToKitchen(savedOrder);
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Order not found with id: " + id);
    }
    
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            
            // Se o pedido foi enviado para a cozinha, sincronizar com o sistema da cozinha
            if (status == OrderStatus.PREPARING) {
                syncOrderToKitchen(updatedOrder);
            }
            
            return updatedOrder;
        }
        throw new RuntimeException("Order not found with id: " + id);
    }
    
    private void syncOrderToKitchen(Order order) {
        try {
            // Verificar se já existe um pedido na cozinha com este orderId
            Optional<KitchenOrder> existingKitchenOrder = kitchenService.getKitchenOrderByOrderId(order.getId());
            
            if (existingKitchenOrder.isPresent()) {
                // Deletar pedido existente na cozinha
                kitchenService.deleteKitchenOrder(existingKitchenOrder.get().getId());
            }
            
            // Criar novo pedido na cozinha
            KitchenOrder kitchenOrder = new KitchenOrder();
            kitchenOrder.setOrderId(order.getId());
            kitchenOrder.setCustomerName(order.getCustomerName());
            kitchenOrder.setCustomerPhone(order.getCustomerPhone());
            kitchenOrder.setDeliveryAddress(order.getDeliveryAddress());
            kitchenOrder.setTableNumber(order.getTableNumber());
            kitchenOrder.setStatus(order.getStatus());
            kitchenOrder.setTotalAmount(order.getTotalAmount());
            kitchenOrder.setCreatedAt(order.getCreatedAt());
            kitchenOrder.setUpdatedAt(order.getUpdatedAt());
            
            // Converter itens do pedido para itens da cozinha
            for (OrderItem orderItem : order.getItems()) {
                KitchenOrderItem kitchenItem = new KitchenOrderItem();
                kitchenItem.setDishId(orderItem.getDishId());
                kitchenItem.setDishName(orderItem.getDishName());
                kitchenItem.setQuantity(orderItem.getQuantity());
                kitchenItem.setUnitPrice(orderItem.getUnitPrice());
                kitchenItem.setTotalPrice(orderItem.getTotalPrice());
                kitchenItem.setPreparationStatus(KitchenOrderItem.PreparationStatus.PENDING);
                kitchenItem.setKitchenOrder(kitchenOrder);
                kitchenOrder.getItems().add(kitchenItem);
            }
            
            // Recalcular total após adicionar itens
            kitchenOrder.calculateTotal();
            
            kitchenService.createKitchenOrder(kitchenOrder);
            
            System.out.println("Pedido sincronizado com a cozinha: " + order.getId() + " - Itens: " + order.getItems().size());
        } catch (Exception e) {
            // Log do erro mas não falha a operação principal
            System.err.println("Erro ao sincronizar pedido com a cozinha: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Order addItemToOrder(Long orderId, OrderItem item) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            
            // Check if item already exists
            Optional<OrderItem> existingItem = order.getItems().stream()
                .filter(i -> i.getDishId().equals(item.getDishId()))
                .findFirst();
            
            if (existingItem.isPresent()) {
                // Update quantity
                OrderItem existing = existingItem.get();
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
            } else {
                // Add new item
                order.addItem(item);
            }
            
            order.calculateTotal();
            Order savedOrder = orderRepository.save(order);
            
            // Se o pedido já foi enviado para a cozinha, sincronizar as mudanças
            if (order.getStatus() == OrderStatus.PREPARING) {
                syncOrderToKitchen(savedOrder);
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }
    
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            
            // Encontrar e remover o item usando removeIf (com orphanRemoval = true isso vai funcionar)
            boolean removed = order.getItems().removeIf(item -> item.getId().equals(itemId));
            
            if (!removed) {
                throw new RuntimeException("Item not found with id: " + itemId);
            }
            
            order.calculateTotal();
            Order savedOrder = orderRepository.save(order);
            
            // Se o pedido já foi enviado para a cozinha, sincronizar as mudanças
            if (order.getStatus() == OrderStatus.PREPARING) {
                syncOrderToKitchen(savedOrder);
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }
    
    public Order updateItemQuantity(Long orderId, Long itemId, Integer quantity) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                });
            order.calculateTotal();
            Order savedOrder = orderRepository.save(order);
            
            // Se o pedido já foi enviado para a cozinha, sincronizar as mudanças
            if (order.getStatus() == OrderStatus.PREPARING) {
                syncOrderToKitchen(savedOrder);
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}

