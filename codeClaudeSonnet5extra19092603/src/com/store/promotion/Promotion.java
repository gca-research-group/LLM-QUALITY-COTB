package com.store.promotion;

import com.store.model.Order;

public interface Promotion {
    String getDescription();

    boolean isApplicable(Order order);

    double calculateDiscount(Order order);
}