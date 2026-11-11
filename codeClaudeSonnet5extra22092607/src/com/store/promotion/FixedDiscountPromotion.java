package com.store.promotion;

import com.store.exception.InvalidAmountException;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.model.Order;

public class FixedDiscountPromotion implements Promotion {
    private final String name;
    private final double fixedAmount;
    private final double threshold;
    private final LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String name, double fixedAmount, double threshold, LoyaltyTier requiredTier) {
        if (fixedAmount < 0) {
            throw new InvalidAmountException("Promotion discount cannot be negative: " + name);
        }
        if (threshold < 0) {
            throw new InvalidAmountException("Promotion threshold cannot be negative: " + name);
        }
        this.name = name;
        this.fixedAmount = fixedAmount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        if (order.getSubtotal() < threshold) {
            return false;
        }
        if (requiredTier == null) {
            return true;
        }
        return customer != null && customer.getLoyaltyTier() == requiredTier;
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        return Math.min(fixedAmount, order.getSubtotal());
    }
}