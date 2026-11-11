package store.model;

import java.util.List;

public class BuyXGetYPromotion implements Promotion {
    private String productId;
    private int x;
    private int y;

    public BuyXGetYPromotion(String productId, int x, int y) {
        if (productId == null || x <= 0 || y <= 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.productId = productId;
        this.x = x;
        this.y = y;
    }

    public double calculateDiscount(Order order) {
        List<OrderItem> items = order.getItems();

        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);

            if (item.getProduct().getId().equals(productId)) {
                int groupSize = x + y;
                int groups = item.getQuantity() / groupSize;
                int remainder = item.getQuantity() % groupSize;
                int freeQuantity = groups * y;

                if (remainder > x) {
                    freeQuantity += remainder - x;
                }

                return freeQuantity * item.getUnitPrice();
            }
        }

        return 0;
    }
}