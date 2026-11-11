package store.model;

public class BuyXGetYPromotion implements Promotion {
    private final String productId;
    private final int buy;
    private final int free;

    public BuyXGetYPromotion(String productId, int buy, int free) {
        if (buy <= 0 || free <= 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.productId = productId;
        this.buy = buy;
        this.free = free;
    }

    @Override
    public double discount(Order order) {
        for (Order.OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                int groups = item.getQuantity() / (buy + free);
                return groups * free * item.getUnitPrice();
            }
        }

        return 0;
    }
}