package com.ibeus.Comanda.Digital.repository;

import com.ibeus.Comanda.Digital.model.KitchenOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {
    
    List<KitchenOrder> findByStatus(KitchenOrder.KitchenOrderStatus status);
    
    List<KitchenOrder> findByTableNumber(Integer tableNumber);
    
    List<KitchenOrder> findByOrderId(Long orderId);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status IN :statuses ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findByStatusIn(@Param("statuses") List<KitchenOrder.KitchenOrderStatus> statuses);
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status != 'SERVED' AND ko.status != 'CANCELLED' ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findActiveKitchenOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'NEW' ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findNewOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'PREPARING' ORDER BY ko.priority DESC, ko.createdAt ASC")
    List<KitchenOrder> findPreparingOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.status = 'READY' ORDER BY ko.createdAt ASC")
    List<KitchenOrder> findReadyOrders();
    
    @Query("SELECT ko FROM KitchenOrder ko WHERE ko.tableNumber = :tableNumber AND ko.status != 'SERVED' AND ko.status != 'CANCELLED' ORDER BY ko.createdAt DESC")
    List<KitchenOrder> findActiveOrdersByTable(@Param("tableNumber") Integer tableNumber);
}

