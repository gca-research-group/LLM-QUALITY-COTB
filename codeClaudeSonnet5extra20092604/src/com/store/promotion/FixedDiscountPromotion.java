package com.store.promotion;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.order.Order;

public class FixedDiscountPromotion implements Promotion {
    private final String name;
    private final double discountValue;
    private final double minimumSubtotal;
    private final LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String name, double discountValue, double minimumSubtotal, LoyaltyTier requiredTier) {
        if (discountValue < 0) {
            throw new StoreOperationException("Discount value cannot be negative");
        }
        if (minimumSubtotal < 0) {
            throw new StoreOperationException("Threshold cannot be negative");
        }
        this.name = name;
        this.discountValue = discountValue;
        this.minimumSubtotal = minimumSubtotal;
        this.requiredTier = requiredTier;
    }

    public String getName() {
        return name;
    }

    public boolean isApplicable(Order order, Customer customer) {
        boolean subtotalOk = order.getSubtotal() >= minimumSubtotal;
        boolean tierOk = requiredTier == null || (customer != null && customer.getTier() == requiredTier);
        return subtotalOk && tierOk;
    }

    public double calculateDiscount(Order order, Customer customer) {
        if (!isApplicable(order, customer)) {
            return 0.0;
        }
        return Math.min(discountValue, order.getSubtotal());
    }
}