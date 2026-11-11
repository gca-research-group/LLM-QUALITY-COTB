package store.model;

public class PercentagePromotion implements Promotion {
    private final double percent;
    private final double threshold;

    public PercentagePromotion(double percent, double threshold) {
        if (percent < 0 || percent > 100 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.percent = percent;
        this.threshold = threshold;
    }

    @Override
    public double discount(Order order) {
        if (order.getSubtotal() >= threshold) {
            return order.getSubtotal() * percent / 100.0;
        }

        return 0;
    }
}