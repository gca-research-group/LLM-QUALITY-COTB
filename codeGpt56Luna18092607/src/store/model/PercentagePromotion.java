package store.model;

public class PercentagePromotion implements Promotion {
    private double threshold;
    private double percentage;

    public PercentagePromotion(double threshold, double percentage) {
        if (threshold < 0 || percentage < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.threshold = threshold;
        this.percentage = percentage;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() >= threshold) {
            return order.getSubtotal() * percentage / 100.0;
        }

        return 0;
    }
}