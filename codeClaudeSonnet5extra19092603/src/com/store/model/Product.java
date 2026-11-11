package com.store.model;

import com.store.exception.InvalidOperationException;

public class Product {
    private final String id;
    private final String name;
    private final double unitPrice;
    private final Category category;
    private boolean active;
    private int stockQuantity;

    public Product(String id, String name, double unitPrice, Category category) {
        if (unitPrice < 0) {
            throw new InvalidOperationException("Unit price cannot be negative for product " + id);
        }
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
        this.stockQuantity = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Stock increase quantity must be positive for product " + id);
        }
        stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidOperationException("Stock decrease quantity must be positive for product " + id);
        }
        if (stockQuantity - quantity < 0) {
            throw new InvalidOperationException("Stock cannot become negative for product " + id);
        }
        stockQuantity -= quantity;
    }
}