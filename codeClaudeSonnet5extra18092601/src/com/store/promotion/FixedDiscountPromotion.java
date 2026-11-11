package com.store.promotion;

import com.store.order.Order;
import com.store.people.LoyaltyTier;

public class FixedDiscountPromotion implements Promotion {
    private final String id;
    private final double discountValue;
    private final double minimumSubtotal;
    private final LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String id, double discountValue, double minimumSubtotal, LoyaltyTier requiredTier) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Promotion id cannot be null or empty");
        }
        if (discountValue < 0) {
            throw new IllegalArgumentException("Discount value cannot be negative");
        }
        if (minimumSubtotal < 0) {
            throw new IllegalArgumentException("Minimum subtotal cannot be negative");
        }
        this.id = id;
        this.discountValue = discountValue;
        this.minimumSubtotal = minimumSubtotal;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isApplicable(Order order) {
        boolean subtotalOk = order.getSubtotal() >= minimumSubtotal;
        boolean tierOk = requiredTier == null || order.getCustomer().getLoyaltyTier() == requiredTier;
        return subtotalOk && tierOk;
    }

    @Override
    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        return discountValue;
    }

    @Override
    public String getDescription() {
        return "Fixed discount promotion (" + discountValue + " for subtotal >= " + minimumSubtotal
                + (requiredTier != null ? ", tier=" + requiredTier : "") + ")";
    }
}