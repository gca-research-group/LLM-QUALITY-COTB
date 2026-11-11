package com.store.shipping;

import com.store.order.Order;

public class StandardShipping implements ShippingRule {
    private final double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping value cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getCode() {
        return "STANDARD";
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }

    @Override
    public String getDescription() {
        return "Standard shipping (" + cost + ")";
    }
}