package store.model;

public class ExpressShipping implements ShippingRule {
    private final double cost;

    public ExpressShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Invalid shipping");
        }
        this.cost = cost;
    }

    public String getName() {
        return "EXPRESS";
    }

    public double calculate(Order order) {
        return cost;
    }
}