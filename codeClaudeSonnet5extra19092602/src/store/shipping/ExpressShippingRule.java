package store.shipping;

import store.model.Order;

public class ExpressShippingRule implements ShippingRule {
    private final double cost;

    public ExpressShippingRule(double cost) {
        this.cost = cost;
    }

    @Override
    public ShippingType getType() {
        return ShippingType.EXPRESS;
    }

    @Override
    public double calculateCost(Order order) {
        return cost;
    }
}