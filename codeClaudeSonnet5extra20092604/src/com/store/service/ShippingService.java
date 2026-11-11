package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.shipping.ShippingRule;

import java.util.HashMap;
import java.util.Map;

public class ShippingService {
    private final Map<String, ShippingRule> rules = new HashMap<String, ShippingRule>();

    public void registerRule(String key, ShippingRule rule) {
        rules.put(key, rule);
    }

    public ShippingRule getRule(String key) {
        ShippingRule rule = rules.get(key);
        if (rule == null) {
            throw new StoreOperationException("Shipping rule not found: " + key);
        }
        return rule;
    }
}