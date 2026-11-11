package com.store.model;

import com.store.exception.InvalidAmountException;
import com.store.exception.InvalidQuantityException;

public class RestockOperation {
    private final String id;
    private final String productId;
    private final String supplierId;
    private final int quantity;
    private final double unitCost;
    private final double totalCost;

    public RestockOperation(String id, String productId, String supplierId, int quantity, double unitCost) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Restock quantity must be positive for product " + productId);
        }
        if (unitCost < 0) {
            throw new InvalidAmountException("Restock unit cost cannot be negative for product " + productId);
        }
        this.id = id;
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = quantity * unitCost;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return totalCost;
    }
}