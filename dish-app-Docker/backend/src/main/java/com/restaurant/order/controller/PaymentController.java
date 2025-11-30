package com.restaurant.order.controller;

import com.restaurant.order.model.Payment;
import com.restaurant.order.model.Payment.PaymentMethod;
import com.restaurant.order.model.Payment.PaymentStatus;
import com.restaurant.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
    
    @GetMapping("/order/{orderId}")
    public List<Payment> getPaymentsByOrderId(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrderId(orderId);
    }
    
    @GetMapping("/order/{orderId}/completed")
    public List<Payment> getCompletedPaymentsByOrderId(@PathVariable Long orderId) {
        return paymentService.getCompletedPaymentsByOrderId(orderId);
    }
    
    @GetMapping("/status/{status}")
    public List<Payment> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status);
    }
    
    @GetMapping("/method/{method}")
    public List<Payment> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        return paymentService.getPaymentsByMethod(method);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
            .map(payment -> ResponseEntity.ok().body(payment))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Payment createPayment(@RequestBody Map<String, Object> paymentData) {
        Long orderId = Long.valueOf(paymentData.get("orderId").toString());
        Double amount = Double.valueOf(paymentData.get("amount").toString());
        PaymentMethod method = PaymentMethod.valueOf(paymentData.get("paymentMethod").toString());
        
        return paymentService.createPayment(orderId, amount, method);
    }
    
    @PostMapping("/{id}/process-cash")
    public ResponseEntity<Payment> processCashPayment(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> cashData) {
        try {
            Double cashReceived = Double.valueOf(cashData.get("cashReceived").toString());
            String processedBy = cashData.get("processedBy").toString();
            
            Payment updatedPayment = paymentService.processCashPayment(id, cashReceived, processedBy);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/process-card")
    public ResponseEntity<Payment> processCardPayment(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> cardData) {
        try {
            String transactionId = cardData.get("transactionId").toString();
            String cardLastFour = cardData.get("cardLastFour").toString();
            String processedBy = cardData.get("processedBy").toString();
            
            Payment updatedPayment = paymentService.processCardPayment(id, transactionId, cardLastFour, processedBy);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/process-pix")
    public ResponseEntity<Payment> processPixPayment(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> pixData) {
        try {
            String transactionId = pixData.get("transactionId").toString();
            String processedBy = pixData.get("processedBy").toString();
            
            Payment updatedPayment = paymentService.processPixPayment(id, transactionId, processedBy);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            PaymentStatus status = PaymentStatus.valueOf(statusUpdate.get("status"));
            Payment updatedPayment = paymentService.updatePaymentStatus(id, status);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/notes")
    public ResponseEntity<Payment> addPaymentNotes(@PathVariable Long id, @RequestBody Map<String, String> notesUpdate) {
        try {
            Payment updatedPayment = paymentService.addPaymentNotes(id, notesUpdate.get("notes"));
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueByDateRange(
            @RequestParam LocalDateTime startDate, 
            @RequestParam LocalDateTime endDate) {
        try {
            Double totalRevenue = paymentService.getTotalRevenueByDateRange(startDate, endDate);
            Long completedPayments = paymentService.getCompletedPaymentsCountByDateRange(startDate, endDate);
            List<Object[]> revenueByMethod = paymentService.getRevenueByPaymentMethod(startDate, endDate);
            
            Map<String, Object> response = Map.of(
                "totalRevenue", totalRevenue,
                "completedPayments", completedPayments,
                "revenueByMethod", revenueByMethod
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/processed-by/{processedBy}")
    public List<Payment> getPaymentsByProcessedBy(@PathVariable String processedBy) {
        return paymentService.getPaymentsByProcessedBy(processedBy);
    }
    
    @GetMapping("/order/{orderId}/fully-paid")
    public ResponseEntity<Map<String, Boolean>> isOrderFullyPaid(@PathVariable Long orderId) {
        boolean fullyPaid = paymentService.isOrderFullyPaid(orderId);
        return ResponseEntity.ok(Map.of("fullyPaid", fullyPaid));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
