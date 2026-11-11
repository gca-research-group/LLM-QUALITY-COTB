package com.store.shipping;

import com.store.exception.StoreOperationException;
import com.store.order.Order;

public class ExpressShipping implements ShippingRule {
    private final double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new StoreOperationException("Shipping cost cannot be negative");
        }
        this.cost = cost;
    }

    public String getName() {
        return "EXPRESS";
    }

    public double calculateCost(Order order) {
        return cost;
    }
}