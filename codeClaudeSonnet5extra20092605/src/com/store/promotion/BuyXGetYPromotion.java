package com.store.promotion;

import com.store.model.Order;
import com.store.model.OrderItem;

public class BuyXGetYPromotion implements Promotion {
    private final String id;
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String id, String productId, int buyQuantity, int freeQuantity) {
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
    public boolean isApplicable(Order order) {
        OrderItem item = findMatchingItem(order);
        return item != null && item.getQuantity() >= (buyQuantity + freeQuantity);
    }

    private OrderItem findMatchingItem(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public double calculateDiscount(Order order) {
        OrderItem item = findMatchingItem(order);
        if (item == null) {
            return 0.0;
        }
        int groupSize = buyQuantity + freeQuantity;
        if (groupSize <= 0) {
            return 0.0;
        }
        int freeUnits = (item.getQuantity() / groupSize) * freeQuantity;
        return freeUnits * item.getUnitPrice();
    }

    @Override
    public String getDescription() {
        return "Buy " + buyQuantity + " get " + freeQuantity + " free on product " + productId;
    }
}