package store.model;

import store.model.StoreTypes.*;

public final class Rules {
    private Rules() {}
    public interface Promotion { double discount(Order order); }
    public static final class PercentagePromotion implements Promotion {
        private final double rate; private final double threshold;
        public PercentagePromotion(double rate, double threshold) { if (rate < 0 || rate > 100 || threshold < 0) throw new IllegalArgumentException("invalid promotion"); this.rate = rate; this.threshold = threshold; }
        public double discount(Order order) { return order.subtotal() >= threshold ? order.subtotal() * rate / 100.0 : 0; }
    }
    public static final class FixedPromotion implements Promotion {
        private final double amount; private final double threshold; private final LoyaltyTier tier;
        public FixedPromotion(double amount, double threshold, LoyaltyTier tier) { if (amount < 0 || threshold < 0) throw new IllegalArgumentException("invalid promotion"); this.amount = amount; this.threshold = threshold; this.tier = tier; }
        public double discount(Order order) { return order.subtotal() >= threshold && (tier == null || order.getCustomer().getLoyaltyTier() == tier) ? Math.min(amount, order.subtotal()) : 0; }
    }
    public static final class BuyXGetYPromotion implements Promotion {
        private final String productId; private final int buy; private final int free;
        public BuyXGetYPromotion(String productId, int buy, int free) { if (buy <= 0 || free <= 0) throw new IllegalArgumentException("invalid promotion"); this.productId = productId; this.buy = buy; this.free = free; }
        public double discount(Order order) { return order.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToDouble(i -> (i.getQuantity() / (double)(buy + free)) * free * i.getUnitPrice()).sum(); }
    }
    public interface ShippingRule { double cost(Order order); }
    public static final class StandardShipping implements ShippingRule { private final double cost; public StandardShipping(double cost) { if (cost < 0) throw new IllegalArgumentException("invalid shipping"); this.cost = cost; } public double cost(Order order) { return cost; } }
    public static final class ExpressShipping implements ShippingRule { private final double cost; public ExpressShipping(double cost) { if (cost < 0) throw new IllegalArgumentException("invalid shipping"); this.cost = cost; } public double cost(Order order) { return cost; } }
    public static final class PickupShipping implements ShippingRule { public double cost(Order order) { return 0; } }
}
