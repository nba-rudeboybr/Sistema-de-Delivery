package com.ibeus.Comanda.Digital.service;

import com.ibeus.Comanda.Digital.model.KitchenOrder;
import com.ibeus.Comanda.Digital.model.KitchenOrderItem;
import com.ibeus.Comanda.Digital.repository.KitchenOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    public KitchenOrder createKitchenOrder(KitchenOrder kitchenOrder) {
        kitchenOrder.setCreatedAt(LocalDateTime.now());
        kitchenOrder.setUpdatedAt(LocalDateTime.now());
        kitchenOrder.calculateTotal();
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateKitchenOrder(KitchenOrder kitchenOrder) {
        kitchenOrder.setUpdatedAt(LocalDateTime.now());
        kitchenOrder.calculateTotal();
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateOrderStatus(Long id, KitchenOrder.KitchenOrderStatus status) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kitchen order not found"));
        
        kitchenOrder.updateStatus(status);
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateItemStatus(Long orderId, Long itemId, KitchenOrderItem.PreparationStatus status) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Kitchen order not found"));
        
        KitchenOrderItem item = kitchenOrder.getItems().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Kitchen order item not found"));
        
        item.updatePreparationStatus(status);
        kitchenOrder.setUpdatedAt(LocalDateTime.now());
        
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public KitchenOrder updateOrderPriority(Long id, Integer priority) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kitchen order not found"));
        
        kitchenOrder.setPriority(priority);
        kitchenOrder.setUpdatedAt(LocalDateTime.now());
        
        return kitchenOrderRepository.save(kitchenOrder);
    }
    
    public void deleteKitchenOrder(Long id) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Kitchen order not found"));
        
        kitchenOrderRepository.delete(kitchenOrder);
    }
    
    // Métodos para estatísticas
    public long countOrdersByStatus(KitchenOrder.KitchenOrderStatus status) {
        return kitchenOrderRepository.findByStatus(status).size();
    }
    
    public long countActiveOrders() {
        return kitchenOrderRepository.findActiveKitchenOrders().size();
    }
    
    public long countNewOrders() {
        return kitchenOrderRepository.findNewOrders().size();
    }
    
    public long countPreparingOrders() {
        return kitchenOrderRepository.findPreparingOrders().size();
    }
    
    public long countReadyOrders() {
        return kitchenOrderRepository.findReadyOrders().size();
    }
}

