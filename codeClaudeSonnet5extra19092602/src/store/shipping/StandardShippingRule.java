package store.shipping;

import store.model.Order;

public class StandardShippingRule implements ShippingRule {
    private final double cost;

    public StandardShippingRule(double cost) {
        this.cost = cost;
    }

    @Override
    public ShippingType getType() {
        return ShippingType.STANDARD;
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }
}