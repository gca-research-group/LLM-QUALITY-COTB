package com.store.shipping;

import com.store.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShippingService {
    private final Map<String, ShippingRule> rules = new LinkedHashMap<>();

    public void registerRule(ShippingRule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule cannot be null");
        }
        if (rules.containsKey(rule.getCode())) {
            throw new IllegalArgumentException("Duplicate shipping code: " + rule.getCode());
        }
        rules.put(rule.getCode(), rule);
    }

    public ShippingRule findByCode(String code) {
        ShippingRule rule = rules.get(code);
        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule not found: " + code);
        }
        return rule;
    }

    public void applyShipping(String code, Order order) {
        ShippingRule rule = findByCode(code);
        double cost = rule.calculateCost(order);
        order.applyShipping(cost, rule.getDescription());
    }

    public List<ShippingRule> all() {
        return new ArrayList<>(rules.values());
    }
}