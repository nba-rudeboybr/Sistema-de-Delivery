package com.restaurant.order.repository;

import com.restaurant.order.model.KitchenOrder;
import com.restaurant.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {
    
    List<KitchenOrder> findByStatus(OrderStatus status);
    
    List<KitchenOrder> findByTableNumber(Integer tableNumber);
    
    Optional<KitchenOrder> findByOrderId(Long orderId);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status IN ('NEW', 'PREPARING', 'READY') ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findActiveKitchenOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'NEW' ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findNewOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'PREPARING' ORDER BY ko.startedAt ASC")
    List<KitchenOrder> findPreparingOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'READY' ORDER BY ko.readyAt ASC")
    List<KitchenOrder> findReadyOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'DELIVERED' AND ko.updatedAt >= :since ORDER BY ko.updatedAt DESC")
    List<KitchenOrder> findRecentlyDeliveredOrders(@Param("since") LocalDateTime since);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.priority = :priority ORDER BY ko.createdAt ASC")
    List<KitchenOrder> findByPriority(@Param("priority") Integer priority);
    
    @Query("SELECT COUNT(ko) FROM KitchenOrder ko WHERE ko.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.createdAt >= :startDate AND ko.createdAt <= :endDate ORDER BY ko.createdAt DESC")
    List<KitchenOrder> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.tableNumber = :tableNumber AND ko.status IN ('NEW', 'PREPARING', 'READY', 'DELIVERED') ORDER BY ko.createdAt DESC")
    List<KitchenOrder> findActiveOrdersByTable(@Param("tableNumber") Integer tableNumber);
}
