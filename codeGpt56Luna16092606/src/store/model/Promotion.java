package store.model;

public interface Promotion {
    String getName();
    double calculateDiscount(Order order);
}