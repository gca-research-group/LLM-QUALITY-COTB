package store.model;

public final class Promotions {
    private Promotions() {
    }

    public static class PercentageThreshold implements Promotion {
        private final double rate;
        private final double threshold;

        public PercentageThreshold(double rate, double threshold) {
            if (rate < 0 || threshold < 0) throw new IllegalArgumentException();
            this.rate = rate;
            this.threshold = threshold;
        }

        public String getName() {
            return "Percentage " + rate * 100 + "%";
        }

        public double discount(Order order) {
            return order.subtotal() >= threshold ? order.subtotal() * rate : 0;
        }
    }

    public static class FixedCustomerThreshold implements Promotion {
        private final double amount;
        private final double threshold;
        private final Customer.LoyaltyTier tier;

        public FixedCustomerThreshold(double amount, double threshold, Customer.LoyaltyTier tier) {
            if (amount < 0 || threshold < 0 || tier == null) throw new IllegalArgumentException();
            this.amount = amount;
            this.threshold = threshold;
            this.tier = tier;
        }

        public String getName() {
            return "Fixed " + amount + " for " + tier;
        }

        public double discount(Order order) {
            return order.subtotal() >= threshold && order.getCustomer().getLoyaltyTier() == tier ? amount : 0;
        }
    }

    public static class BuyXGetY implements Promotion {
        private final String productId;
        private final int x;
        private final int y;

        public BuyXGetY(String productId, int x, int y) {
            if (productId == null || x <= 0 || y <= 0) throw new IllegalArgumentException();
            this.productId = productId;
            this.x = x;
            this.y = y;
        }

        public String getName() {
            return "Buy " + x + " Get " + y;
        }

        public double discount(Order order) {
            double discount = 0;
            for (Order.Item item : order.getItems()) {
                if (item.getProduct().getId().equals(productId)) {
                    int free = (item.getQuantity() / (x + y)) * y;
                    discount = free * item.getUnitPrice();
                }
            }
            return discount;
        }
    }
}