package com.store.shipping;

import com.store.model.Order;

public class PickupShipping implements ShippingRule {
    @Override
    public String getName() {
        return "PICKUP";
    }

    @Override
    public double calculateCost(Order order) {
        return 0.0;
    }
}