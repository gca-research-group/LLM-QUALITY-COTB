package com.store.service;

import com.store.exception.EntityNotFoundException;
import com.store.shipping.ShippingRule;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ShippingService {
    private final Map<String, ShippingRule> rules = new LinkedHashMap<>();

    public void register(ShippingRule rule) {
        rules.put(rule.getName(), rule);
    }

    public ShippingRule getByName(String name) {
        ShippingRule rule = rules.get(name);
        if (rule == null) {
            throw new EntityNotFoundException("Shipping rule not found: " + name);
        }
        return rule;
    }

    public Collection<ShippingRule> getAll() {
        return rules.values();
    }
}