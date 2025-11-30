package com.ibeus.Comanda.Digital.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;
    
    @Column(name = "dish_name", nullable = false)
    private String dishName;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "total_price", nullable = false)
    private Double totalPrice;
    
    @Column(name = "notes")
    private String notes;
    
    // Constructors
    public OrderItem() {}
    
    public OrderItem(Dish dish, Integer quantity) {
        this.dish = dish;
        this.dishName = dish.getName();
        this.quantity = quantity;
        this.unitPrice = dish.getPrice();
        this.totalPrice = dish.getPrice() * quantity;
    }
    
    public OrderItem(Dish dish, Integer quantity, String notes) {
        this(dish, quantity);
        this.notes = notes;
    }
    
    // Método para calcular o preço total
    public void calculateTotal() {
        this.totalPrice = this.unitPrice * this.quantity;
    }
    
    // Método para atualizar quantidade
    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        calculateTotal();
    }
    
    // Método para atualizar o nome do prato quando o prato for alterado
    public void setDish(Dish dish) {
        this.dish = dish;
        if (dish != null) {
            this.dishName = dish.getName();
        }
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Dish getDish() { return dish; }
    
    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
