package com.store.model;

import com.store.exception.InvalidOperationException;

public class RestockEntry {
    private final String id;
    private final Product product;
    private final Supplier supplier;
    private final int quantity;
    private final double unitCost;

    public RestockEntry(String id, Product product, Supplier supplier, int quantity, double unitCost) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Restock quantity must be positive");
        }
        if (unitCost < 0) {
            throw new InvalidOperationException("Restock unit cost cannot be negative");
        }
        this.id = id;
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double getTotalCost() {
        return quantity * unitCost;
    }
}