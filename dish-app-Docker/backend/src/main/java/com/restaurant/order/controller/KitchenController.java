package com.restaurant.order.controller;

import com.restaurant.order.model.KitchenOrder;
import com.restaurant.order.model.KitchenOrderItem;
import com.restaurant.order.model.OrderStatus;
import com.restaurant.order.service.KitchenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kitchen")
@CrossOrigin(origins = "http://localhost:4200")
public class KitchenController {
    
    @Autowired
    private KitchenService kitchenService;
    
    @GetMapping("/orders")
    public List<KitchenOrder> getAllKitchenOrders() {
        return kitchenService.getAllKitchenOrders();
    }
    
    @GetMapping("/orders/active")
    public List<KitchenOrder> getActiveKitchenOrders() {
        return kitchenService.getActiveKitchenOrders();
    }
    
    @GetMapping("/orders/new")
    public List<KitchenOrder> getNewOrders() {
        return kitchenService.getNewOrders();
    }
    
    @GetMapping("/orders/preparing")
    public List<KitchenOrder> getPreparingOrders() {
        return kitchenService.getPreparingOrders();
    }
    
    @GetMapping("/orders/ready")
    public List<KitchenOrder> getReadyOrders() {
        return kitchenService.getReadyOrders();
    }
    
    @GetMapping("/orders/table/{tableNumber}")
    public List<KitchenOrder> getOrdersByTable(@PathVariable Integer tableNumber) {
        return kitchenService.getOrdersByTable(tableNumber);
    }
    
    @GetMapping("/orders/{id}")
    public ResponseEntity<KitchenOrder> getKitchenOrderById(@PathVariable Long id) {
        return kitchenService.getKitchenOrderById(id)
            .map(order -> ResponseEntity.ok().body(order))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/orders")
    public KitchenOrder createKitchenOrder(@RequestBody KitchenOrder kitchenOrder) {
        return kitchenService.createKitchenOrder(kitchenOrder);
    }
    
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<KitchenOrder> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            OrderStatus status = OrderStatus.valueOf(statusUpdate.get("status"));
            KitchenOrder updatedOrder = kitchenService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{orderId}/items/{itemId}/status")
    public ResponseEntity<KitchenOrder> updateItemPreparationStatus(
            @PathVariable Long orderId, 
            @PathVariable Long itemId, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            KitchenOrderItem.PreparationStatus status = KitchenOrderItem.PreparationStatus.valueOf(statusUpdate.get("status"));
            KitchenOrder updatedOrder = kitchenService.updateItemPreparationStatus(orderId, itemId, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{orderId}/items/{itemId}/notes")
    public ResponseEntity<KitchenOrder> addPreparationNotes(
            @PathVariable Long orderId, 
            @PathVariable Long itemId, 
            @RequestBody Map<String, String> notesUpdate) {
        try {
            KitchenOrder updatedOrder = kitchenService.addPreparationNotes(orderId, itemId, notesUpdate.get("notes"));
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/priority")
    public ResponseEntity<KitchenOrder> updateOrderPriority(@PathVariable Long id, @RequestBody Map<String, Integer> priorityUpdate) {
        try {
            KitchenOrder updatedOrder = kitchenService.updateOrderPriority(id, priorityUpdate.get("priority"));
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/notes")
    public ResponseEntity<KitchenOrder> addOrderNotes(@PathVariable Long id, @RequestBody Map<String, String> notesUpdate) {
        try {
            KitchenOrder updatedOrder = kitchenService.addOrderNotes(id, notesUpdate.get("notes"));
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/estimated-time")
    public ResponseEntity<KitchenOrder> updateEstimatedTime(@PathVariable Long id, @RequestBody Map<String, Integer> timeUpdate) {
        try {
            KitchenOrder updatedOrder = kitchenService.updateEstimatedTime(id, timeUpdate.get("estimatedTime"));
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/ready")
    public ResponseEntity<KitchenOrder> markOrderAsReady(@PathVariable Long id) {
        try {
            KitchenOrder updatedOrder = kitchenService.markOrderAsReady(id);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/all-items-ready")
    public ResponseEntity<KitchenOrder> markAllItemsAsReady(@PathVariable Long id) {
        try {
            KitchenOrder updatedOrder = kitchenService.markAllItemsAsReady(id);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/delivered")
    public ResponseEntity<KitchenOrder> markOrderAsDelivered(@PathVariable Long id) {
        try {
            KitchenOrder updatedOrder = kitchenService.markOrderAsDelivered(id);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/orders/{id}/cancel")
    public ResponseEntity<KitchenOrder> cancelOrder(@PathVariable Long id) {
        try {
            KitchenOrder updatedOrder = kitchenService.cancelOrder(id);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/orders/priority/{priority}")
    public List<KitchenOrder> getOrdersByPriority(@PathVariable Integer priority) {
        return kitchenService.getOrdersByPriority(priority);
    }
    
    @GetMapping("/orders/count/{status}")
    public Long getOrderCountByStatus(@PathVariable OrderStatus status) {
        return kitchenService.getOrderCountByStatus(status);
    }
    
    @GetMapping("/orders/date-range")
    public List<KitchenOrder> getOrdersByDateRange(
            @RequestParam LocalDateTime startDate, 
            @RequestParam LocalDateTime endDate) {
        return kitchenService.getOrdersByDateRange(startDate, endDate);
    }
    
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteKitchenOrder(@PathVariable Long id) {
        try {
            kitchenService.deleteKitchenOrder(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
