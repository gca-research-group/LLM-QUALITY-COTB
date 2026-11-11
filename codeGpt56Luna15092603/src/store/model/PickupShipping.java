package store.model;

public class PickupShipping implements ShippingRule {
    @Override
    public double cost(Order order) {
        return 0;
    }

    @Override
    public String getName() {
        return "PICKUP";
    }
}