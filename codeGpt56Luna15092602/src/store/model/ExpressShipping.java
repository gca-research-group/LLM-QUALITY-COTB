package store.model;

public class ExpressShipping implements ShippingRule {
    @Override
    public double cost(Order order) {
        return 25.0;
    }

    @Override
    public String getName() {
        return "EXPRESS";
    }
}