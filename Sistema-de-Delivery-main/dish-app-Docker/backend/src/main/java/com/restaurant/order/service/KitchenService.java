package com.restaurant.order.service;

import com.restaurant.order.model.KitchenOrder;
import com.restaurant.order.model.KitchenOrderItem;
import com.restaurant.order.model.OrderStatus;
import com.restaurant.order.repository.KitchenOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KitchenService {
    
    @Autowired
    private KitchenOrderRepository kitchenOrderRepository;
    
    @Autowired
    @Lazy
    private OrderService orderService;
    
    public List<KitchenOrder> getAllKitchenOrders() {
        return kitchenOrderRepository.findAll();
    }
    
    public List<KitchenOrder> getActiveKitchenOrders() {
        return kitchenOrderRepository.findActiveKitchenOrders();
    }
    
    public List<KitchenOrder> getNewOrders() {
        return kitchenOrderRepository.findNewOrders();
    }
    
    public List<KitchenOrder> getPreparingOrders() {
        return kitchenOrderRepository.findPreparingOrders();
    }
    
    public List<KitchenOrder> getReadyOrders() {
        return kitchenOrderRepository.findReadyOrders();
    }
    
    public List<KitchenOrder> getOrdersByTable(Integer tableNumber) {
        return kitchenOrderRepository.findActiveOrdersByTable(tableNumber);
    }
    
    public Optional<KitchenOrder> getKitchenOrderById(Long id) {
        return kitchenOrderRepository.findById(id);
    }
    
    public Optional<KitchenOrder> getKitchenOrderByOrderId(Long orderId) {
        return kitchenOrderRepository.findByOrderId(orderId);
    }
    
    public KitchenOrder createKitchenOrder(KitchenOrder kitchenOrder) {
        kitchenOrder.calculateTotal();
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateKitchenOrder(KitchenOrder kitchenOrder) {
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateOrderStatus(Long id, OrderStatus status) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            
            // Validar transição de status
            if (!order.getStatus().canTransitionTo(status)) {
                throw new IllegalArgumentException("Transição de status inválida: " + 
                    order.getStatus() + " -> " + status);
            }
            
            order.setStatus(status);
            KitchenOrder savedOrder = kitchenOrderRepository.save(order);
            
            // Se o pedido foi marcado como DELIVERED, sincronizar com o pedido principal
            if (status == OrderStatus.DELIVERED) {
                try {
                    orderService.updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED);
                    System.out.println("Pedido principal sincronizado como DELIVERED: " + order.getOrderId());
                } catch (Exception e) {
                    System.err.println("Erro ao sincronizar pedido principal: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Se o pedido foi marcado como READY, sincronizar com o pedido principal
            if (status == OrderStatus.READY) {
                try {
                    orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY);
                    System.out.println("Pedido principal sincronizado como READY: " + order.getOrderId());
                } catch (Exception e) {
                    System.err.println("Erro ao sincronizar pedido principal: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + id);
    }
    
    public KitchenOrder updateItemPreparationStatus(Long orderId, Long itemId, 
                                                  KitchenOrderItem.PreparationStatus status) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setPreparationStatus(status);
                });
            return kitchenOrderRepository.save(order);
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + orderId);
    }
    
    public KitchenOrder addPreparationNotes(Long orderId, Long itemId, String notes) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setPreparationNotes(notes);
                });
            return kitchenOrderRepository.save(order);
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + orderId);
    }
    
    public KitchenOrder updateOrderPriority(Long id, Integer priority) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            order.setPriority(priority);
            return kitchenOrderRepository.save(order);
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + id);
    }
    
    public KitchenOrder addOrderNotes(Long id, String notes) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            order.setNotes(notes);
            return kitchenOrderRepository.save(order);
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + id);
    }
    
    public KitchenOrder updateEstimatedTime(Long id, Integer estimatedTime) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            order.setEstimatedTime(estimatedTime);
            return kitchenOrderRepository.save(order);
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + id);
    }
    
    public List<KitchenOrder> getOrdersByPriority(Integer priority) {
        return kitchenOrderRepository.findByPriority(priority);
    }
    
    public Long getOrderCountByStatus(OrderStatus status) {
        return kitchenOrderRepository.countByStatus(status);
    }
    
    public List<KitchenOrder> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return kitchenOrderRepository.findOrdersByDateRange(startDate, endDate);
    }
    
    public KitchenOrder markAllItemsAsReady(Long orderId) {
        Optional<KitchenOrder> optionalOrder = kitchenOrderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            KitchenOrder order = optionalOrder.get();
            
            // Marcar todos os itens como READY
            order.getItems().forEach(item -> {
                item.setPreparationStatus(KitchenOrderItem.PreparationStatus.READY);
            });
            
            // Atualizar status do pedido para READY
            order.setStatus(OrderStatus.READY);
            order.setReadyAt(LocalDateTime.now());
            
            KitchenOrder savedOrder = kitchenOrderRepository.save(order);
            
            // Sincronizar com o pedido principal
            try {
                orderService.updateOrderStatus(order.getOrderId(), OrderStatus.READY);
                System.out.println("Pedido principal sincronizado como READY: " + order.getOrderId());
            } catch (Exception e) {
                System.err.println("Erro ao sincronizar pedido principal: " + e.getMessage());
                e.printStackTrace();
            }
            
            return savedOrder;
        }
        throw new RuntimeException("Comanda da cozinha não encontrada com id: " + orderId);
    }
    
    public KitchenOrder markOrderAsReady(Long id) {
        return updateOrderStatus(id, OrderStatus.READY);
    }
    
    public KitchenOrder markOrderAsDelivered(Long id) {
        return updateOrderStatus(id, OrderStatus.DELIVERED);
    }
    
    public KitchenOrder cancelOrder(Long id) {
        return updateOrderStatus(id, OrderStatus.CANCELLED);
    }
    
    public void deleteKitchenOrder(Long id) {
        kitchenOrderRepository.deleteById(id);
    }
}
