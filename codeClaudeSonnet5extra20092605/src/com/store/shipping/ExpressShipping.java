package com.store.shipping;

import com.store.model.Order;

public class ExpressShipping implements ShippingRule {
    private final double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getName() {
        return "EXPRESS";
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }
}