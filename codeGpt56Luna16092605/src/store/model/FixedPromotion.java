package store.model;

public class FixedPromotion implements Promotion {
    private final double amount;
    private final double threshold;
    private final LoyaltyTier requiredTier;

    public FixedPromotion(double amount, double threshold, LoyaltyTier requiredTier) {
        if (amount < 0 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.amount = amount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() < threshold) {
            return 0;
        }

        if (requiredTier != null && order.getCustomer().getLoyaltyTier() != requiredTier) {
            return 0;
        }

        return Math.min(amount, order.getSubtotal());
    }
}