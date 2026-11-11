package com.store.promotion;

import com.store.exception.InvalidOperationException;
import com.store.model.LoyaltyTier;
import com.store.model.Order;

public class FixedDiscountForTierPromotion implements Promotion {
    private final double discountValue;
    private final double threshold;
    private final LoyaltyTier requiredTier;

    public FixedDiscountForTierPromotion(double discountValue, double threshold, LoyaltyTier requiredTier) {
        if (discountValue < 0) {
            throw new InvalidOperationException("Discount value cannot be negative");
        }
        this.discountValue = discountValue;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getDescription() {
        return String.format("Fixed discount of %.2f for %s customers with subtotal >= %.2f",
                discountValue, requiredTier, threshold);
    }

    @Override
    public boolean isApplicable(Order order) {
        return order.getCustomer().getTier() == requiredTier && order.getSubtotal() >= threshold;
    }

    @Override
    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        return discountValue;
    }
}