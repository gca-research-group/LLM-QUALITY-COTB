package com.store.promotion;

import com.store.model.LoyaltyTier;
import com.store.model.Order;

public class FixedDiscountCustomerPromotion implements Promotion {
    private final String id;
    private final double discountAmount;
    private final double threshold;
    private final LoyaltyTier requiredTier;

    public FixedDiscountCustomerPromotion(String id, double discountAmount, double threshold, LoyaltyTier requiredTier) {
        this.id = id;
        this.discountAmount = discountAmount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getId() {
        return id;
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
        return Math.min(discountAmount, order.getSubtotal());
    }

    @Override
    public String getDescription() {
        return "Fixed discount of " + discountAmount + " for " + requiredTier + " customers with subtotal >= " + threshold;
    }
}