package com.store.shipping;

import com.store.order.Order;

public class PickupShipping implements ShippingRule {
    public String getName() {
        return "PICKUP";
    }

    public double calculateCost(Order order) {
        return 0.0;
    }
}