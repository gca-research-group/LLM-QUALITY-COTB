package store.model;

public class FixedPromotion implements Promotion {
    private double threshold;
    private double discount;
    private LoyaltyTier requiredTier;

    public FixedPromotion(double threshold, double discount, LoyaltyTier requiredTier) {
        if (threshold < 0 || discount < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.threshold = threshold;
        this.discount = discount;
        this.requiredTier = requiredTier;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() >= threshold &&
            (requiredTier == null || order.getCustomer().getLoyaltyTier() == requiredTier)) {
            return discount;
        }

        return 0;
    }
}