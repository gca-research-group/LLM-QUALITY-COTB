package store.shipping;

import store.model.Order;

public interface ShippingRule {
    String getName();

    double calculateCost(Order order);
}