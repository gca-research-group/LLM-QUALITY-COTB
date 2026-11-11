package store.model;

public class PickupShipping implements ShippingRule {
    public String getName() {
        return "PICKUP";
    }

    public double calculate(Order order) {
        return 0.0;
    }
}