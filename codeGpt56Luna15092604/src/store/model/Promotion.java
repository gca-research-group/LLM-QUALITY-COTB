package store.model;

public interface Promotion {
    double calculateDiscount(Order order);
    String getName();
}