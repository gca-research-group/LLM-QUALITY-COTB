package com.store.promotion;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.order.Order;

public class BuyXGetYPromotion implements Promotion {
    private final String name;
    private final String productId;
    private final int buyQuantity;
    private final int getFreeQuantity;

    public BuyXGetYPromotion(String name, String productId, int buyQuantity, int getFreeQuantity) {
        if (buyQuantity <= 0 || getFreeQuantity <= 0) {
            throw new StoreOperationException("Buy and free quantities must be positive");
        }
        this.name = name;
        this.productId = productId;
        this.buyQuantity = buyQuantity;
        this.getFreeQuantity = getFreeQuantity;
    }

    public String getName() {
        return name;
    }

    public boolean isApplicable(Order order, Customer customer) {
        int qty = order.getPurchasedQuantity(productId);
        return qty >= (buyQuantity + getFreeQuantity);
    }

    public double calculateDiscount(Order order, Customer customer) {
        int qty = order.getPurchasedQuantity(productId);
        int groupSize = buyQuantity + getFreeQuantity;
        if (qty < groupSize) {
            return 0.0;
        }
        int fullGroups = qty / groupSize;
        int freeUnits = fullGroups * getFreeQuantity;
        double unitPrice = order.getUnitPriceForProduct(productId);
        return freeUnits * unitPrice;
    }
}