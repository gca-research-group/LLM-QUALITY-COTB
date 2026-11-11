package com.store.model;

import com.store.exception.InvalidAmountException;
import com.store.exception.InvalidQuantityException;

public class ReturnItem {
    private final String productId;
    private final String productName;
    private final int quantity;
    private final double unitPrice;

    public ReturnItem(String productId, String productName, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Returned quantity must be positive for product " + productId);
        }
        if (unitPrice < 0) {
            throw new InvalidAmountException("Unit price cannot be negative for product " + productId);
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        return quantity * unitPrice;
    }
}