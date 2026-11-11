package store.promotion;

import store.exception.InvalidQuantityException;
import store.model.Order;
import store.model.OrderItem;

public class BuyXGetYPromotion implements Promotion {
    private final String id;
    private final String description;
    private final String productId;
    private final int buyQuantity;
    private final int getQuantity;

    public BuyXGetYPromotion(String id, String description, String productId, int buyQuantity, int getQuantity) {
        if (buyQuantity <= 0 || getQuantity <= 0) {
            throw new InvalidQuantityException("Buy and get quantities must be positive");
        }
        this.id = id;
        this.description = description;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.getQuantity = getQuantity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    private OrderItem findEligibleItem(Order order) {
        return order.findItem(productId);
    }

    @Override
    public boolean isApplicable(Order order) {
        OrderItem item = findEligibleItem(order);
        return item != null && item.getQuantity() >= (buyQuantity + getQuantity);
    }

    @Override
    public double calculateDiscount(Order order) {
        OrderItem item = findEligibleItem(order);
        if (item == null) {
            return 0.0;
        }
        int groupSize = buyQuantity + getQuantity;
        int freeGroups = item.getQuantity() / groupSize;
        int freeUnits = freeGroups * getQuantity;
        return freeUnits * item.getUnitPriceAtOrderTime();
    }
}