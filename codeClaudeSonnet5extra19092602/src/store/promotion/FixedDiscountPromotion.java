package store.promotion;

import store.exception.BusinessRuleException;
import store.model.Customer;
import store.model.LoyaltyTier;
import store.model.Order;

public class FixedDiscountPromotion implements Promotion {
    private final String id;
    private final double discountAmount;
    private final double thresholdSubtotal;
    private final LoyaltyTier requiredTier;

    public FixedDiscountPromotion(String id, double discountAmount, double thresholdSubtotal, LoyaltyTier requiredTier) {
        if (discountAmount < 0) {
            throw new BusinessRuleException("Discount amount cannot be negative for promotion " + id);
        }
        this.id = id;
        this.discountAmount = discountAmount;
        this.thresholdSubtotal = thresholdSubtotal;
        this.requiredTier = requiredTier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return "Fixed discount of " + discountAmount + " for subtotal >= " + thresholdSubtotal
                + (requiredTier != null ? " and tier " + requiredTier : "");
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        boolean thresholdMet = order.getSubtotal() >= thresholdSubtotal;
        boolean tierMet = requiredTier == null || (customer != null && customer.getLoyaltyTier() == requiredTier);
        return thresholdMet && tierMet;
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        if (!isApplicable(order, customer)) {
            return 0.0;
        }
        return Math.min(discountAmount, order.getSubtotal());
    }
}