package store.promotion;

import store.model.Order;

public interface Promotion {
    String getId();

    String getDescription();

    boolean isApplicable(Order order);

    double calculateDiscount(Order order);
}