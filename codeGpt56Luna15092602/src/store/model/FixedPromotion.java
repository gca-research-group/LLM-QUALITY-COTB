package store.model;

public class FixedPromotion implements Promotion {
    private final double amount;
    private final double threshold;
    private final Customer.LoyaltyTier tier;

    public FixedPromotion(double amount, double threshold, Customer.LoyaltyTier tier) {
        if (amount < 0 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.amount = amount;
        this.threshold = threshold;
        this.tier = tier;
    }

    @Override
    public double discount(Order order) {
        if (order.getSubtotal() >= threshold
                && order.getCustomer().getLoyaltyTier() == tier) {
            return Math.min(amount, order.getSubtotal());
        }

        return 0;
    }
}