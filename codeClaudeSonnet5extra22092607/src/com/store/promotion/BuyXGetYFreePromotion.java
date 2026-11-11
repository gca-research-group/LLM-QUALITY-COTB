package com.store.promotion;

import com.store.exception.InvalidQuantityException;
import com.store.model.Customer;
import com.store.model.Order;
import com.store.model.OrderItem;

public class BuyXGetYFreePromotion implements Promotion {
    private final String name;
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYFreePromotion(String name, String productId, int buyQuantity, int freeQuantity) {
        if (buyQuantity <= 0 || freeQuantity <= 0) {
            throw new InvalidQuantityException("Buy and free quantities must be positive: " + name);
        }
        this.name = name;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isApplicable(Order order, Customer customer) {
        OrderItem item = order.findItemByProductId(productId);
        return item != null && item.getQuantity() >= (buyQuantity + freeQuantity);
    }

    @Override
    public double calculateDiscount(Order order, Customer customer) {
        OrderItem item = order.findItemByProductId(productId);
        if (item == null) {
            return 0.0;
        }
        int groupSize = buyQuantity + freeQuantity;
        int freeUnits = (item.getQuantity() / groupSize) * freeQuantity;
        return freeUnits * item.getUnitPrice();
    }
}