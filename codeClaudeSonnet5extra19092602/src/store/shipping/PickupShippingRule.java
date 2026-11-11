package store.shipping;

import store.model.Order;

public class PickupShippingRule implements ShippingRule {
    @Override
    public ShippingType getType() {
        return ShippingType.PICKUP;
    }

    @Override
    public double calculateCost(Order order) {
        return 0.0;
    }
}