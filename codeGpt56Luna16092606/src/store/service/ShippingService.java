package store.service;

import java.util.LinkedHashMap;
import java.util.Map;
import store.model.ShippingMethod;
import store.model.ShippingRule;

public class ShippingService {
    private final Map<ShippingMethod, ShippingRule> rules =
        new LinkedHashMap<ShippingMethod, ShippingRule>();

    public void register(ShippingRule rule) {
        if (rule.getMethod() == ShippingMethod.PICKUP && rule.getCost() != 0) {
            throw new IllegalArgumentException("Pickup must be free");
        }

        rules.put(rule.getMethod(), rule);
    }

    public ShippingRule get(ShippingMethod method) {
        ShippingRule rule = rules.get(method);

        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule not found");
        }

        return rule;
    }
}