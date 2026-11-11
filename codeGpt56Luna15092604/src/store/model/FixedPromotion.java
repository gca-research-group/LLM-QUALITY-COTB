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

    @Override
    public double calculateDiscount(Order order) {
        boolean tierOk = requiredTier == null
                || order.getCustomer().getLoyaltyTier() == requiredTier;

        return tierOk && order.getSubtotal() >= threshold
                ? Math.min(amount, order.getSubtotal())
                : 0;
    }

    @Override
    public String getName() {
        return "Fixed " + amount;
    }
}