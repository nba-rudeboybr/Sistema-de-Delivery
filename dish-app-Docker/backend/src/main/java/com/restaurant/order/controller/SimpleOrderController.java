package com.restaurant.order.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/simple-orders")
@CrossOrigin(origins = "http://localhost:4200")
public class SimpleOrderController {
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        
        // Dados de exemplo
        Map<String, Object> order1 = new HashMap<>();
        order1.put("id", 1);
        order1.put("tableNumber", 5);
        order1.put("customerName", "João Silva");
        order1.put("status", "OPEN");
        order1.put("totalAmount", 45.90);
        order1.put("items", new ArrayList<>());
        orders.add(order1);
        
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("tableNumber", orderData.get("tableNumber"));
        response.put("customerName", orderData.get("customerName"));
        response.put("status", "OPEN");
        response.put("totalAmount", 0.0);
        response.put("items", new ArrayList<>());
        response.put("message", "Comanda criada com sucesso!");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long id) {
        Map<String, Object> order = new HashMap<>();
        order.put("id", id);
        order.put("tableNumber", 5);
        order.put("customerName", "João Silva");
        order.put("status", "OPEN");
        order.put("totalAmount", 45.90);
        order.put("items", new ArrayList<>());
        
        return ResponseEntity.ok(order);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> orderData) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("tableNumber", orderData.get("tableNumber"));
        response.put("customerName", orderData.get("customerName"));
        response.put("status", orderData.get("status"));
        response.put("totalAmount", orderData.get("totalAmount"));
        response.put("items", orderData.get("items"));
        response.put("message", "Comanda atualizada com sucesso!");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Comanda excluída com sucesso!");
        
        return ResponseEntity.ok(response);
    }
}
