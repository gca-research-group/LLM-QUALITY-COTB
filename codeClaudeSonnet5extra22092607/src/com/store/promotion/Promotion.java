package com.store.promotion;

import com.store.model.Customer;
import com.store.model.Order;

public interface Promotion {
    String getName();

    boolean isApplicable(Order order, Customer customer);

    double calculateDiscount(Order order, Customer customer);
}