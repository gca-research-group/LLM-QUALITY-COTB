package store.model;

public interface ShippingRule {
    double cost(Order order);
    String getName();
}