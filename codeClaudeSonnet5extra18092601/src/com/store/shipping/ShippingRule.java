package com.store.shipping;

import com.store.order.Order;

public interface ShippingRule {
    String getCode();

    double calculateCost(Order order);

    String getDescription();
}