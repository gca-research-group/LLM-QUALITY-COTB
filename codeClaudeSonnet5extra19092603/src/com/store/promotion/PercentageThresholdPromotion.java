package com.store.promotion;

import com.store.exception.InvalidOperationException;
import com.store.model.Order;

public class PercentageThresholdPromotion implements Promotion {
    private final double percentage;
    private final double threshold;

    public PercentageThresholdPromotion(double percentage, double threshold) {
        if (percentage < 0) {
            throw new InvalidOperationException("Promotion percentage cannot be negative");
        }
        if (threshold < 0) {
            throw new InvalidOperationException("Promotion threshold cannot be negative");
        }
        this.percentage = percentage;
        this.threshold = threshold;
    }

    @Override
    public String getDescription() {
        return String.format("%.0f%% discount for subtotal >= %.2f", percentage, threshold);
    }

    @Override
    public boolean isApplicable(Order order) {
        return order.getSubtotal() >= threshold;
    }

    @Override
    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        return order.getSubtotal() * (percentage / 100.0);
    }
}