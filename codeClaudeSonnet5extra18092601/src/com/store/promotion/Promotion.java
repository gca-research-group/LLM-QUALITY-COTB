package com.store.promotion;

import com.store.order.Order;

public interface Promotion {
    String getId();

    boolean isApplicable(Order order);

    double calculateDiscount(Order order);

    String getDescription();
}