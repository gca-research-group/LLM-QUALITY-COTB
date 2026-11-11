package store.promotion;

import store.exception.InvalidAmountException;
import store.model.LoyaltyTier;
import store.model.Order;

public class FixedDiscountPromotion implements Promotion {
    private final String id;
    private final String description;
    private final double discountValue;
    private final double subtotalThreshold;
    private final LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String id, String description, double discountValue,
                                   double subtotalThreshold, LoyaltyTier requiredTier) {
        if (discountValue < 0) {
            throw new InvalidAmountException("Discount value cannot be negative");
        }
        if (subtotalThreshold < 0) {
            throw new InvalidAmountException("Subtotal threshold cannot be negative");
        }
        this.id = id;
        this.description = description;
        this.discountValue = discountValue;
        this.subtotalThreshold = subtotalThreshold;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isApplicable(Order order) {
        boolean meetsThreshold = order.getSubtotal() >= subtotalThreshold;
        boolean meetsTier = requiredTier == null || order.getCustomer().getLoyaltyTier() == requiredTier;
        return meetsThreshold && meetsTier;
    }

    @Override
    public double calculateDiscount(Order order) {
        return Math.min(discountValue, order.getSubtotal());
    }
}