package com.store.shipping;

import com.store.model.Order;

public interface ShippingRule {
    String getName();

    double calculateCost(Order order);
}