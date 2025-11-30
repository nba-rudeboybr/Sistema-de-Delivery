package com.restaurant.order.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.PostConstruct;

@Entity
@Table(name = "kitchen_order_items")
public class KitchenOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dish_id", nullable = false)
    private Long dishId;
    
    @Column(name = "dish_name", nullable = false)
    private String dishName;
    
    @Column(name = "dish_description")
    private String dishDescription;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preparation_status", nullable = false)
    private PreparationStatus preparationStatus = PreparationStatus.PENDING;
    
    @Column(name = "preparation_notes")
    private String preparationNotes;
    
    @Column(name = "estimated_prep_time")
    private Integer estimatedPrepTime; // em minutos
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_order_id")
    @JsonIgnore
    private KitchenOrder kitchenOrder;
    
    // Enum para status de preparo
    public enum PreparationStatus {
        PENDING("Pendente"),
        IN_PROGRESS("Em Preparo"),
        READY("Pronto"),
        SERVED("Servido");
        
        private final String description;
        
        PreparationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // Constructors
    public KitchenOrderItem() {}
    
    public KitchenOrderItem(Long dishId, String dishName, String dishDescription, 
                           Integer quantity, Double unitPrice, Integer estimatedPrepTime) {
        this.dishId = dishId;
        this.dishName = dishName;
        this.dishDescription = dishDescription;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity * unitPrice;
        this.estimatedPrepTime = estimatedPrepTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDishId() {
        return dishId;
    }
    
    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
    
    public String getDishName() {
        return dishName;
    }
    
    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
    
    public String getDishDescription() {
        return dishDescription;
    }
    
    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null) {
            this.totalPrice = this.quantity * this.unitPrice;
        }
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        if (this.quantity != null) {
            this.totalPrice = this.quantity * this.unitPrice;
        }
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public PreparationStatus getPreparationStatus() {
        return preparationStatus;
    }
    
    public void setPreparationStatus(PreparationStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }
    
    public String getPreparationNotes() {
        return preparationNotes;
    }
    
    public void setPreparationNotes(String preparationNotes) {
        this.preparationNotes = preparationNotes;
    }
    
    public Integer getEstimatedPrepTime() {
        return estimatedPrepTime;
    }
    
    public void setEstimatedPrepTime(Integer estimatedPrepTime) {
        this.estimatedPrepTime = estimatedPrepTime;
    }
    
    public KitchenOrder getKitchenOrder() {
        return kitchenOrder;
    }
    
    public void setKitchenOrder(KitchenOrder kitchenOrder) {
        this.kitchenOrder = kitchenOrder;
    }
    
    @PostConstruct
    public void calculateTotalPrice() {
        if (this.quantity != null && this.unitPrice != null) {
            this.totalPrice = this.quantity * this.unitPrice;
        }
    }
}
