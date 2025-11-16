package com.restaurant.order.controller;

import com.restaurant.order.model.Order;
import com.restaurant.order.model.OrderItem;
import com.restaurant.order.model.OrderStatus;
import com.restaurant.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(order -> ResponseEntity.ok().body(order))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/table/{tableNumber}")
    public List<Order> getOrdersByTable(@PathVariable Integer tableNumber) {
        return orderService.getOrdersByTable(tableNumber);
    }
    
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        try {
            Order updatedOrder = orderService.updateOrder(id, order);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            System.out.println("StatusUpdate recebido: " + statusUpdate);
            String statusString = statusUpdate.get("status");
            System.out.println("Status string extraído: " + statusString);
            OrderStatus status = OrderStatus.valueOf(statusString);
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao converter status: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/items")
    public ResponseEntity<Order> addItemToOrder(@PathVariable Long id, @RequestBody OrderItem item) {
        try {
            Order updatedOrder = orderService.addItemToOrder(id, item);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Order> removeItemFromOrder(@PathVariable Long id, @PathVariable Long itemId) {
        try {
            Order updatedOrder = orderService.removeItemFromOrder(id, itemId);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/items/{itemId}")
    public ResponseEntity<Order> updateItemQuantity(@PathVariable Long id, @PathVariable Long itemId, @RequestBody Map<String, Integer> quantityUpdate) {
        try {
            Integer quantity = quantityUpdate.get("quantity");
            Order updatedOrder = orderService.updateItemQuantity(id, itemId, quantity);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

