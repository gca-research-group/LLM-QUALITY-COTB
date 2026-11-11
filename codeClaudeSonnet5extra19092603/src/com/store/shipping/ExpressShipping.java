package com.store.shipping;

import com.store.exception.InvalidOperationException;

public class ExpressShipping implements ShippingRule {
    private final double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new InvalidOperationException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getDescription() {
        return "EXPRESS";
    }

    @Override
    public double calculateCost() {
        return cost;
    }
}