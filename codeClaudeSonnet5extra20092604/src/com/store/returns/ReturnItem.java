package com.store.returns;

import com.store.exception.StoreOperationException;
import com.store.model.Product;

public class ReturnItem {
    private final Product product;
    private final int quantity;
    private final double unitPrice;

    public ReturnItem(Product product, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new StoreOperationException("Returned quantity must be positive");
        }
        if (unitPrice < 0) {
            throw new StoreOperationException("Unit price cannot be negative");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
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