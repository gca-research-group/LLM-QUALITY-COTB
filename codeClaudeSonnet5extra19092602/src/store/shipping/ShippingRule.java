package store.shipping;

import store.model.Order;

public interface ShippingRule {
    ShippingType getType();

    double calculateCost(Order order);
}