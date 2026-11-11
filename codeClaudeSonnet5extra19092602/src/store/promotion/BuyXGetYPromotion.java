package store.promotion;

import store.exception.BusinessRuleException;
import store.model.Customer;
import store.model.Order;
import store.model.OrderItem;

public class BuyXGetYPromotion implements Promotion {
    private final String id;
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String id, String productId, int buyQuantity, int freeQuantity) {
        if (buyQuantity <= 0 || freeQuantity <= 0) {
            throw new BusinessRuleException("Buy and free quantities must be positive for promotion " + id);
        }
        this.id = id;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDescription() {
        return "Buy " + buyQuantity + " get " + freeQuantity + " free on product " + productId;
    }

    private int getQuantityInOrder(Order order) {
        int quantity = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                quantity += item.getQuantity();
            }
        }
        return quantity;
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        return getQuantityInOrder(order) >= buyQuantity + freeQuantity;
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        int quantity = getQuantityInOrder(order);
        int groupSize = buyQuantity + freeQuantity;
        if (quantity < groupSize) {
            return 0.0;
        }
        int groups = quantity / groupSize;
        int freeUnits = groups * freeQuantity;
        double unitPrice = 0.0;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                unitPrice = item.getUnitPrice();
                break;
            }
        }
        return freeUnits * unitPrice;
    }
}