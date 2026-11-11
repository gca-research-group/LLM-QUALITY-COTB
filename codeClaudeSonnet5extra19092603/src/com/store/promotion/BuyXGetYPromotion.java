package com.store.promotion;

import com.store.exception.InvalidOperationException;
import com.store.model.Order;
import com.store.model.OrderItem;

public class BuyXGetYPromotion implements Promotion {
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String productId, int buyQuantity, int freeQuantity) {
        if (buyQuantity <= 0) {
            throw new InvalidOperationException("Buy quantity must be positive");
        }
        if (freeQuantity <= 0) {
            throw new InvalidOperationException("Free quantity must be positive");
        }
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    @Override
    public String getDescription() {
        return String.format("Buy %d get %d free on product %s", buyQuantity, freeQuantity, productId);
    }

    @Override
    public boolean isApplicable(Order order) {
        return findMatchingItem(order) != null;
    }

    @Override
    public double calculateDiscount(Order order) {
        OrderItem item = findMatchingItem(order);
        if (item == null) {
            return 0.0;
        }
        int groupSize = buyQuantity + freeQuantity;
        int fullGroups = item.getQuantity() / groupSize;
        int freeUnits = fullGroups * freeQuantity;
        return freeUnits * item.getUnitPrice();
    }

    private OrderItem findMatchingItem(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}