package com.store.promotion;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.order.Order;

public class PercentagePromotion implements Promotion {
    private final String name;
    private final double percentage;
    private final double minimumSubtotal;

    public PercentagePromotion(String name, double percentage, double minimumSubtotal) {
        if (percentage < 0) {
            throw new StoreOperationException("Discount percentage cannot be negative");
        }
        if (minimumSubtotal < 0) {
            throw new StoreOperationException("Threshold cannot be negative");
        }
        this.name = name;
        this.percentage = percentage;
        this.minimumSubtotal = minimumSubtotal;
    }

    public String getName() {
        return name;
    }

    public boolean isApplicable(Order order, Customer customer) {
        return order.getSubtotal() >= minimumSubtotal;
    }

    public double calculateDiscount(Order order, Customer customer) {
        if (!isApplicable(order, customer)) {
            return 0.0;
        }
        return order.getSubtotal() * (percentage / 100.0);
    }
}