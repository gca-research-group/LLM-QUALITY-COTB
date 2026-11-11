package store.model;

public class StandardShipping implements ShippingRule {
    private final double cost;

    public StandardShipping(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Invalid shipping");
        }
        this.cost = cost;
    }

    public String getName() {
        return "STANDARD";
    }

    public double calculate(Order order) {
        return cost;
    }
}