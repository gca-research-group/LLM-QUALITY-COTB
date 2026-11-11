package store.model;

public class ShippingRule {
    private ShippingMethod method;
    private double cost;

    public ShippingRule(ShippingMethod method, double cost) {
        if (method == null || cost < 0 || (method == ShippingMethod.PICKUP && cost != 0)) {
            throw new IllegalArgumentException("Invalid shipping");
        }

        this.method = method;
        this.cost = cost;
    }

    public ShippingMethod getMethod() {
        return method;
    }

    public double getCost() {
        return cost;
    }
}