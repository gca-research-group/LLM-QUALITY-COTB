package com.store.restock;

public class RestockEntry {
    private final String id;
    private final String productId;
    private final String supplierId;
    private final int quantity;
    private final double unitCost;

    public RestockEntry(String id, String productId, String supplierId, int quantity, double unitCost) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Restock id cannot be null or empty");
        }
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be null or empty");
        }
        if (supplierId == null || supplierId.isEmpty()) {
            throw new IllegalArgumentException("Supplier id cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantities must be positive");
        }
        if (unitCost < 0) {
            throw new IllegalArgumentException("Restock unit cost cannot be negative");
        }
        this.id = id;
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.unitCost = unitCost;
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
        return quantity * unitCost;
    }
}