package store.model;

public class StandardShipping implements ShippingRule {
    private final double value;

    public StandardShipping(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid shipping value");
        }

        this.value = value;
    }

    @Override
    public double cost(Order order) {
        return value;
    }

    @Override
    public String getName() {
        return "STANDARD";
    }
}