package com.store.returns;

import com.store.catalog.Product;

public class ReturnItem {
    private final Product product;
    private final int quantity;

    public ReturnItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
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
        return quantity * unitPrice;
    }
}