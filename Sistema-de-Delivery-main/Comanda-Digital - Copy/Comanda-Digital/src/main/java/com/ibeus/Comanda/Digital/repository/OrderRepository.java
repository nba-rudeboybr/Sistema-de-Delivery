package com.ibeus.Comanda.Digital.repository;

import com.ibeus.Comanda.Digital.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByTableNumber(Integer tableNumber);
    
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt DESC")
    List<Order> findByStatusIn(@Param("statuses") List<Order.OrderStatus> statuses);
    
    @Query("SELECT o FROM Order o WHERE o.status != 'PAID' AND o.status != 'CANCELLED' ORDER BY o.createdAt DESC")
    List<Order> findActiveOrders();
    
    @Query("SELECT o FROM Order o WHERE o.tableNumber = :tableNumber AND o.status != 'PAID' AND o.status != 'CANCELLED' ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByTable(@Param("tableNumber") Integer tableNumber);
}

