package store.model;

public interface Promotion {
    String getName();
    double discount(Order order);
}