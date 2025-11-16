package com.restaurant.order.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kitchen_orders")
public class KitchenOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "table_number", nullable = false)
    private Integer tableNumber;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @OneToMany(mappedBy = "kitchenOrder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<KitchenOrderItem> items = new ArrayList<>();
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;
    
    @Column(name = "estimated_time")
    private Integer estimatedTime; // em minutos
    
    @Column(name = "priority")
    private Integer priority = 1; // 1 = normal, 2 = alta, 3 = urgente
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "ready_at")
    private LocalDateTime readyAt;
    
    // Constructors
    public KitchenOrder() {}
    
    public KitchenOrder(Long orderId, Integer tableNumber, String customerName, OrderStatus status) {
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Integer getTableNumber() {
        return tableNumber;
    }
    
    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        
        if (status == OrderStatus.PREPARING && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        
        if (status == OrderStatus.READY && this.readyAt == null) {
            this.readyAt = LocalDateTime.now();
        }
    }
    
    public List<KitchenOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<KitchenOrderItem> items) {
        this.items = items;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getEstimatedTime() {
        return estimatedTime;
    }
    
    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getReadyAt() {
        return readyAt;
    }
    
    public void setReadyAt(LocalDateTime readyAt) {
        this.readyAt = readyAt;
    }
    
    // Helper methods
    public void addItem(KitchenOrderItem item) {
        items.add(item);
        item.setKitchenOrder(this);
        calculateTotal();
    }
    
    public void removeItem(KitchenOrderItem item) {
        items.remove(item);
        item.setKitchenOrder(null);
        calculateTotal();
    }
    
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(KitchenOrderItem::getTotalPrice)
            .sum();
    }
    
    public long getPreparationTime() {
        if (startedAt != null && readyAt != null) {
            return java.time.Duration.between(startedAt, readyAt).toMinutes();
        }
        return 0;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
