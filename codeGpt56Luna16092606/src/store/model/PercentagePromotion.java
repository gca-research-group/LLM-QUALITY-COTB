package store.model;

public class PercentagePromotion implements Promotion {
    private final String name;
    private final double percentage;
    private final double threshold;

    public PercentagePromotion(String name, double percentage, double threshold) {
        if (percentage < 0 || percentage > 100 || threshold < 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.name = name;
        this.percentage = percentage;
        this.threshold = threshold;
    }

    public String getName() {
        return name;
    }

    public double calculateDiscount(Order order) {
        if (order.getSubtotal() >= threshold) {
            return order.getSubtotal() * percentage / 100.0;
        }

        return 0;
    }
}