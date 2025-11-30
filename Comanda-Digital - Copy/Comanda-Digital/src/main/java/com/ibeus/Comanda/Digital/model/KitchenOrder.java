package com.ibeus.Comanda.Digital.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kitchen_orders")
@Data
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
    private KitchenOrderStatus status = KitchenOrderStatus.NEW;
    
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
    
    // Enum para status do pedido na cozinha
    public enum KitchenOrderStatus {
        NEW("Novo Pedido"),
        PREPARING("Preparando"),
        READY("Pronto"),
        SERVED("Servido"),
        CANCELLED("Cancelado");
        
        private final String description;
        
        KitchenOrderStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean isActive() {
            return this != SERVED && this != CANCELLED;
        }
    }
    
    // Constructors
    public KitchenOrder() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public KitchenOrder(Long orderId, Integer tableNumber, String customerName) {
        this();
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.customerName = customerName;
    }
    
    // Método para calcular o total
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(KitchenOrderItem::getTotalPrice)
            .sum();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Método para adicionar item
    public void addItem(KitchenOrderItem item) {
        item.setKitchenOrder(this);
        this.items.add(item);
        calculateTotal();
    }
    
    // Método para remover item
    public void removeItem(KitchenOrderItem item) {
        this.items.remove(item);
        item.setKitchenOrder(null);
        calculateTotal();
    }
    
    // Método para atualizar status
    public void updateStatus(KitchenOrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        if (newStatus == KitchenOrderStatus.PREPARING && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        } else if (newStatus == KitchenOrderStatus.READY && this.readyAt == null) {
            this.readyAt = LocalDateTime.now();
        }
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Integer getTableNumber() { return tableNumber; }
    public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public KitchenOrderStatus getStatus() { return status; }
    public void setStatus(KitchenOrderStatus status) { this.status = status; }
    
    public List<KitchenOrderItem> getItems() { return items; }
    public void setItems(List<KitchenOrderItem> items) { this.items = items; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public Integer getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getReadyAt() { return readyAt; }
    public void setReadyAt(LocalDateTime readyAt) { this.readyAt = readyAt; }
}
