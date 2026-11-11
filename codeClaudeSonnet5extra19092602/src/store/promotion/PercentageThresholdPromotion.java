package store.promotion;

import store.exception.BusinessRuleException;
import store.model.Customer;
import store.model.Order;

public class PercentageThresholdPromotion implements Promotion {
    private final String id;
    private final double percentage;
    private final double thresholdSubtotal;

    public PercentageThresholdPromotion(String id, double percentage, double thresholdSubtotal) {
        if (percentage < 0) {
            throw new BusinessRuleException("Discount percentage cannot be negative for promotion " + id);
        }
        this.id = id;
        this.percentage = percentage;
        this.thresholdSubtotal = thresholdSubtotal;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return percentage * 100 + "% discount for orders with subtotal >= " + thresholdSubtotal;
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        return order.getSubtotal() >= thresholdSubtotal;
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        if (!isApplicable(order, customer)) {
            return 0.0;
        }
        return order.getSubtotal() * percentage;
    }
}