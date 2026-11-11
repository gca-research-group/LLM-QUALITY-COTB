package store.service;

import store.exception.BusinessRuleException;
import store.shipping.ShippingRule;
import store.shipping.ShippingType;

import java.util.EnumMap;
import java.util.Map;

public class ShippingService {
    private final Map<ShippingType, ShippingRule> rules = new EnumMap<>(ShippingType.class);

    public void registerRule(ShippingRule rule) {
        rules.put(rule.getType(), rule);
    }

    public ShippingRule findRule(ShippingType type) {
        ShippingRule rule = rules.get(type);
        if (rule == null) {
            throw new BusinessRuleException("Shipping rule not registered: " + type);
        }
        return rule;
    }
}