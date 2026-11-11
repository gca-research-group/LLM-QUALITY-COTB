package store.model;

public class ExpressShipping implements ShippingRule {
    private final double value;

    public ExpressShipping(double value) {
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
        return "EXPRESS";
    }
}