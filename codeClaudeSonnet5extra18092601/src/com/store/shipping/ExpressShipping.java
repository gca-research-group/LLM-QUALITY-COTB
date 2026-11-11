package com.store.shipping;

import com.store.order.Order;

public class ExpressShipping implements ShippingRule {
    private final double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping value cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getCode() {
        return "EXPRESS";
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }

    @Override
    public String getDescription() {
        return "Express shipping (" + cost + ")";
    }
}