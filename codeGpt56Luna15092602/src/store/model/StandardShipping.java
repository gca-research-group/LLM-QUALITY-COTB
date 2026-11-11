package store.model;

public class StandardShipping implements ShippingRule {
    @Override
    public double cost(Order order) {
        return 12.0;
    }

    @Override
    public String getName() {
        return "STANDARD";
    }
}