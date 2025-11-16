package com.restaurant.order.model;

public enum OrderStatus {
    NEW("Novo Pedido"),
    PREPARING("Preparando"),
    READY("Pronto"),
    DELIVERED("Entregue"),
    PAID("Pago"),
    CANCELLED("Cancelado");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this != PAID && this != CANCELLED;
    }
    
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case NEW:
                return newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING:
                return newStatus == READY || newStatus == CANCELLED;
            case READY:
                return newStatus == DELIVERED || newStatus == CANCELLED;
            case DELIVERED:
                return newStatus == PAID;
            case PAID:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
}

