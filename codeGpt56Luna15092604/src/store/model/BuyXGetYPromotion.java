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

    @Override
    public double calculateDiscount(Order order) {
        return order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .mapToDouble(i ->
                        (i.getQuantity() / (double) (buy + get))
                                * get
                                * i.getUnitPrice())
                .sum();
    }

    @Override
    public String getName() {
        return "Buy " + buy + " Get " + get;
    }
}