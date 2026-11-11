package com.store.promotion;

import com.store.order.Order;

public class PercentagePromotion implements Promotion {
    private final String id;
    private final double percentage;
    private final double minimumSubtotal;

    public PercentagePromotion(String id, double percentage, double minimumSubtotal) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Promotion id cannot be null or empty");
        }
        if (percentage < 0) {
            throw new IllegalArgumentException("Percentage cannot be negative");
        }
        if (minimumSubtotal < 0) {
            throw new IllegalArgumentException("Minimum subtotal cannot be negative");
        }
        this.id = id;
        this.percentage = percentage;
        this.minimumSubtotal = minimumSubtotal;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isApplicable(Order order) {
        return order.getSubtotal() >= minimumSubtotal;
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
        return "Percentage promotion (" + percentage + "% for subtotal >= " + minimumSubtotal + ")";
    }
}