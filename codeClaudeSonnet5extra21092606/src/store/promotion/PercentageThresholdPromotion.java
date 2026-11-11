package store.promotion;

import store.exception.InvalidAmountException;
import store.model.Order;

public class PercentageThresholdPromotion implements Promotion {
    private final String id;
    private final String description;
    private final double percentage;
    private final double subtotalThreshold;

    public PercentageThresholdPromotion(String id, String description, double percentage, double subtotalThreshold) {
        if (percentage < 0) {
            throw new InvalidAmountException("Percentage cannot be negative");
        }
        if (subtotalThreshold < 0) {
            throw new InvalidAmountException("Subtotal threshold cannot be negative");
        }
        this.id = id;
        this.description = description;
        this.percentage = percentage;
        this.subtotalThreshold = subtotalThreshold;
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
        return order.getSubtotal() >= subtotalThreshold;
    }

    @Override
    public double calculateDiscount(Order order) {
        return order.getSubtotal() * percentage;
    }
}