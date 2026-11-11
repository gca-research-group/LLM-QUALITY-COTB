package store.promotion;

import store.model.Customer;
import store.model.Order;

public interface Promotion {
    String getId();

    String getDescription();

    boolean isApplicable(Order order, Customer customer);

    double calculateDiscount(Order order, Customer customer);
}