package com.store.model;

public class Payment {
    private final String id;
    private final Order order;
    private final double amount;

    public Payment(String id, Order order, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }
        this.id = id;
        this.order = order;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public double getAmount() {
        return amount;
    }
}