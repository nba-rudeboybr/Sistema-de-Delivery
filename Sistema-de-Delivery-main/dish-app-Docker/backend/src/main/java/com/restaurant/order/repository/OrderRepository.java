package com.restaurant.order.repository;

import com.restaurant.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByTableNumber(Integer tableNumber);
    
    List<Order> findByStatus(com.restaurant.order.model.OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.status IN ('OPEN', 'IN_PROGRESS') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrders();
    
    @Query("SELECT o FROM Order o WHERE o.tableNumber = :tableNumber AND o.status IN ('OPEN', 'IN_PROGRESS')")
    List<Order> findActiveOrdersByTable(@Param("tableNumber") Integer tableNumber);
}

