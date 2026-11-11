package com.store.shipping;

import com.store.order.Order;

public class PickupShipping implements ShippingRule {

    @Override
    public String getCode() {
        return "PICKUP";
    }

    @Override
    public double calculateCost(Order order) {
        return 0.0;
    }

    @Override
    public String getDescription() {
        return "Pickup shipping (0.0)";
    }
}