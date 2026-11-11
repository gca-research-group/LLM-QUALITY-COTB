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

    @Override
    public double calculateDiscount(Order order) {
        return order.getSubtotal() >= threshold
                ? order.getSubtotal() * percentage / 100.0
                : 0;
    }

    @Override
    public String getName() {
        return percentage + "% over " + threshold;
    }
}