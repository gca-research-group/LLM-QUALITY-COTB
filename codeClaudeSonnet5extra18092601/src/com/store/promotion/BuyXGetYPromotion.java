package com.store.promotion;

import com.store.order.Order;
import com.store.order.OrderItem;

public class BuyXGetYPromotion implements Promotion {
    private final String id;
    private final String productId;
    private final int buyQuantity;
    private final int freeQuantity;

    public BuyXGetYPromotion(String id, String productId, int buyQuantity, int freeQuantity) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Promotion id cannot be null or empty");
        }
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be null or empty");
        }
        if (buyQuantity <= 0 || freeQuantity <= 0) {
            throw new IllegalArgumentException("Buy and free quantities must be positive");
        }
        this.id = id;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    private int quantityOfProduct(Order order) {
        int total = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isApplicable(Order order) {
        return quantityOfProduct(order) >= buyQuantity;
    }

    @Override
    public double calculateDiscount(Order order) {
        if (!isApplicable(order)) {
            return 0.0;
        }
        int ownedQuantity = quantityOfProduct(order);
        int groups = ownedQuantity / buyQuantity;
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

    @Override
    public String getDescription() {
        return "Buy " + buyQuantity + " get " + freeQuantity + " free promotion on product " + productId;
    }
}