package com.store.promotion;

import com.store.exception.InvalidAmountException;
import com.store.model.Customer;
import com.store.model.Order;

public class PercentageThresholdPromotion implements Promotion {
    private final String name;
    private final double percentage;
    private final double threshold;

    public PercentageThresholdPromotion(String name, double percentage, double threshold) {
        if (percentage < 0) {
            throw new InvalidAmountException("Promotion percentage cannot be negative: " + name);
        }
        if (threshold < 0) {
            throw new InvalidAmountException("Promotion threshold cannot be negative: " + name);
        }
        this.name = name;
        this.percentage = percentage;
        this.threshold = threshold;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        return order.getSubtotal() >= threshold;
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        return order.getSubtotal() * percentage;
    }
}