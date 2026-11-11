package com.store.model;

public class ReturnItem {
    private final Product product;
    private final int quantity;

    public ReturnItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Returned quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getRefundAmount(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        return quantity * unitPrice;
    }
}