package com.store.promotion;

import com.store.model.Order;

public class PercentageThresholdPromotion implements Promotion {
    private final String id;
    private final double percentage;
    private final double threshold;

    public PercentageThresholdPromotion(String id, double percentage, double threshold) {
        this.id = id;
        this.percentage = percentage;
        this.threshold = threshold;
    }

    @Override
    public String getId() {
        return id;
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

    @Override
    public String getDescription() {
        return percentage + "% discount for subtotal >= " + threshold;
    }
}