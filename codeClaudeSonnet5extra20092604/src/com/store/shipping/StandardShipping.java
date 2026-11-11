package com.store.shipping;

import com.store.exception.StoreOperationException;
import com.store.order.Order;

public class StandardShipping implements ShippingRule {
    private final double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new StoreOperationException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    public String getName() {
        return "STANDARD";
    }

    public double calculateCost(Order order) {
        return cost;
    }
}