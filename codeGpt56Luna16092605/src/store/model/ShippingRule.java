package store.model;

public interface ShippingRule {
    String getName();
    double calculate(Order order);
}