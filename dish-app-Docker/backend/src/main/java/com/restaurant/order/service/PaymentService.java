package com.restaurant.order.service;

import com.restaurant.order.model.Payment;
import com.restaurant.order.model.Payment.PaymentMethod;
import com.restaurant.order.model.Payment.PaymentStatus;
import com.restaurant.order.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public List<Payment> getCompletedPaymentsByOrderId(Long orderId) {
        return paymentRepository.findCompletedPaymentsByOrderId(orderId);
    }
    
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }
    
    public List<Payment> getPaymentsByMethod(PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Payment createPayment(Long orderId, Double amount, PaymentMethod paymentMethod) {
        Payment payment = new Payment(orderId, amount, paymentMethod);
        return paymentRepository.save(payment);
    }
    
    public Payment processCashPayment(Long paymentId, Double cashReceived, String processedBy) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            
            if (payment.getPaymentMethod() != PaymentMethod.CASH) {
                throw new IllegalArgumentException("Este pagamento não é em dinheiro");
            }
            
            if (cashReceived < payment.getAmount()) {
                throw new IllegalArgumentException("Valor recebido é menor que o valor do pagamento");
            }
            
            payment.setCashReceived(cashReceived);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedBy(processedBy);
            payment.setProcessedAt(LocalDateTime.now());
            
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado com id: " + paymentId);
    }
    
    public Payment processCardPayment(Long paymentId, String transactionId, String cardLastFour, String processedBy) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            
            if (payment.getPaymentMethod() == PaymentMethod.CASH) {
                throw new IllegalArgumentException("Este pagamento é em dinheiro, não cartão");
            }
            
            payment.setTransactionId(transactionId);
            payment.setCardLastFour(cardLastFour);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedBy(processedBy);
            payment.setProcessedAt(LocalDateTime.now());
            
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado com id: " + paymentId);
    }
    
    public Payment processPixPayment(Long paymentId, String transactionId, String processedBy) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            
            if (payment.getPaymentMethod() != PaymentMethod.PIX) {
                throw new IllegalArgumentException("Este pagamento não é PIX");
            }
            
            payment.setTransactionId(transactionId);
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProcessedBy(processedBy);
            payment.setProcessedAt(LocalDateTime.now());
            
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado com id: " + paymentId);
    }
    
    public Payment updatePaymentStatus(Long id, PaymentStatus status) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus(status);
            if (status == PaymentStatus.COMPLETED && payment.getProcessedAt() == null) {
                payment.setProcessedAt(LocalDateTime.now());
            }
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado com id: " + id);
    }
    
    public Payment addPaymentNotes(Long id, String notes) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setNotes(notes);
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Pagamento não encontrado com id: " + id);
    }
    
    public Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Double total = paymentRepository.getTotalRevenueByDateRange(startDate, endDate);
        return total != null ? total : 0.0;
    }
    
    public List<Object[]> getRevenueByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.getRevenueByPaymentMethod(startDate, endDate);
    }
    
    public Long getCompletedPaymentsCountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.countCompletedPaymentsByDateRange(startDate, endDate);
    }
    
    public List<Payment> getPaymentsByProcessedBy(String processedBy) {
        return paymentRepository.findByProcessedBy(processedBy);
    }
    
    public boolean isOrderFullyPaid(Long orderId) {
        List<Payment> completedPayments = getCompletedPaymentsByOrderId(orderId);
        // Aqui você pode implementar lógica para verificar se o total pago é igual ao valor do pedido
        return !completedPayments.isEmpty();
    }
    
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
