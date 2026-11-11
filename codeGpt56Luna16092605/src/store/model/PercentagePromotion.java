package store.model;

public class PercentagePromotion implements Promotion {
    private final double percentage;
    private final double threshold;

    public PercentagePromotion(double percentage, double threshold) {
        if (percentage < 0 || percentage > 100 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.percentage = percentage;
        this.threshold = threshold;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() < threshold) {
            return 0;
        }

        return order.getSubtotal() * percentage / 100.0;
    }
}