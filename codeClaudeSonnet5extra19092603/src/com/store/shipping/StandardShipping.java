package com.store.shipping;

import com.store.exception.InvalidOperationException;

public class StandardShipping implements ShippingRule {
    private final double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new InvalidOperationException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    @Override
    public String getDescription() {
        return "STANDARD";
    }

    @Override
    public double calculateCost() {
        return cost;
    }
}