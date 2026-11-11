package store.model;

public class BuyXGetYPromotion implements Promotion {
    private final String productId;
    private final int buy;
    private final int get;

    public BuyXGetYPromotion(String productId, int buy, int get) {
        if (productId == null || buy <= 0 || get <= 0) {
            throw new IllegalArgumentException("Invalid promotion");
        }

        this.productId = productId;
        this.buy = buy;
        this.get = get;
    }

    public double calculateDiscount(Order order) {
        double discount = 0;

        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                int free = (item.getQuantity() / (buy + get)) * get;
                discount = free * item.getUnitPrice();
            }
        }

        return discount;
    }
}