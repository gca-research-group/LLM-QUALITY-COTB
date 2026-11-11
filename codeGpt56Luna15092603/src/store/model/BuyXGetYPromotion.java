package store.model;

public class BuyXGetYPromotion implements Promotion {
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String productId, int buyQuantity, int freeQuantity) {
        if (productId == null || productId.isBlank() || buyQuantity <= 0 || freeQuantity <= 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    @Override
    public double discount(Order order) {
        for (Order.OrderItem item : order.getItems()) {
            if (item.getProductId().equals(productId)) {
                int groupSize = buyQuantity + freeQuantity;
                int groups = item.getQuantity() / groupSize;
                return groups * freeQuantity * item.getUnitPrice();
            }
        }

        return 0;
    }

    @Override
    public String getName() {
        return "Buy " + buyQuantity + " get " + freeQuantity + " free for " + productId;
    }
}