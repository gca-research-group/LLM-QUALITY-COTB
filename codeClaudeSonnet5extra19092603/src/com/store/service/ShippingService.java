package com.store.service;

import com.store.model.Order;
import com.store.shipping.ShippingRule;

public class ShippingService {
    public void applyShipping(Order order, ShippingRule rule) {
        order.setShippingCost(rule.calculateCost(), rule.getDescription());
    }
}