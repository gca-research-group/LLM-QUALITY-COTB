package store.model;

public class BuyXGetYPromotion implements Promotion {
    private final String name;
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String name, String productId, int buyQuantity, int freeQuantity) {
        if (buyQuantity <= 0 || freeQuantity <= 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.name = name;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    public String getName() {
        return name;
    }

    public double calculateDiscount(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                int free = (item.getQuantity() / buyQuantity) * freeQuantity;

                if (free > item.getQuantity()) {
                    free = item.getQuantity();
                }

                return free * item.getUnitPrice();
            }
        }

        return 0;
    }
}