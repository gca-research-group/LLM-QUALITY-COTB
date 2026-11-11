package store.model;

public class PercentagePromotion implements Promotion {
    private final double percent;
    private final double threshold;

    public PercentagePromotion(double percent, double threshold) {
        if (percent < 0 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.percent = percent;
        this.threshold = threshold;
    }

    @Override
    public double discount(Order order) {
        if (order.subtotal() < threshold) {
            return 0;
        }

        return order.subtotal() * percent / 100.0;
    }

    @Override
    public String getName() {
        return percent + "% discount over " + threshold;
    }
}