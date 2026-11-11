package store.model;

public class FixedPromotion implements Promotion {
    private final String name;
    private final double amount;
    private final double threshold;
    private final Customer.LoyaltyTier requiredTier;

    public FixedPromotion(String name, double amount, double threshold, Customer.LoyaltyTier requiredTier) {
        if (amount < 0 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.name = name;
        this.amount = amount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    public String getName() {
        return name;
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