package com.store.shipping;

import com.store.order.Order;

public interface ShippingRule {
    String getName();

    double calculateCost(Order order);
}