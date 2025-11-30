package com.restaurant.order.repository;

import com.restaurant.order.model.Payment;
import com.restaurant.order.model.Payment.PaymentStatus;
import com.restaurant.order.model.Payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.status = 'COMPLETED'")
    List<Payment> findCompletedPaymentsByOrderId(@Param("orderId") Long orderId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    Double getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate AND p.createdAt <= :endDate GROUP BY p.paymentMethod")
    List<Object[]> getRevenueByPaymentMethod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt >= :startDate AND p.createdAt <= :endDate")
    Long countCompletedPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.processedBy = :processedBy ORDER BY p.processedAt DESC")
    List<Payment> findByProcessedBy(@Param("processedBy") String processedBy);
}
