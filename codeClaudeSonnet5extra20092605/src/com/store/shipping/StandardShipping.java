package com.store.shipping;

import com.store.model.Order;

public class StandardShipping implements ShippingRule {
    private final double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getName() {
        return "STANDARD";
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }
}