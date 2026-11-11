package store.model;

public class FixedPromotion implements Promotion {
    private final double amount;
    private final double threshold;
    private final Customer.LoyaltyTier requiredTier;

    public FixedPromotion(double amount, double threshold, Customer.LoyaltyTier requiredTier) {
        if (amount < 0 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.amount = amount;
        this.threshold = threshold;
        this.requiredTier = requiredTier;
    }

    @Override
    public double discount(Order order) {
        boolean tierSatisfied = requiredTier == null
                || order.getCustomer().getLoyaltyTier() == requiredTier;

        if (tierSatisfied && order.subtotal() >= threshold) {
            return amount;
        }

        return 0;
    }

    @Override
    public String getName() {
        return "Fixed discount " + amount;
    }
}