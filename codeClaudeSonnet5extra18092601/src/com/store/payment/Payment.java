package com.store.payment;

public class Payment {
    private final String id;
    private final String orderId;
    private final double amount;

    public Payment(String id, String orderId, double amount) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Payment id cannot be null or empty");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Order id cannot be null or empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Payment value cannot be negative");
        }
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }
}