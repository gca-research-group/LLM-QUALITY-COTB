package com.store.model;

import com.store.exception.InvalidAmountException;
import com.store.exception.InvalidQuantityException;
import com.store.exception.ReturnQuantityExceededException;

public class OrderItem {
    private final String productId;
    private final String productName;
    private final int quantity;
    private final double unitPrice;
    private int returnedQuantity;

    public OrderItem(String productId, String productName, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Order item quantity must be positive for product " + productId);
        }
        if (unitPrice < 0) {
            throw new InvalidAmountException("Unit price cannot be negative for product " + productId);
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.returnedQuantity = 0;
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

    public int getReturnedQuantity() {
        return returnedQuantity;
    }

    public int getRemainingReturnable() {
        return quantity - returnedQuantity;
    }

    public void markReturned(int qty) {
        if (qty <= 0) {
            throw new InvalidQuantityException("Returned quantity must be positive for product " + productId);
        }
        if (qty > getRemainingReturnable()) {
            throw new ReturnQuantityExceededException("Returned quantity exceeds remaining returnable quantity for product " + productId);
        }
        returnedQuantity += qty;
    }
}