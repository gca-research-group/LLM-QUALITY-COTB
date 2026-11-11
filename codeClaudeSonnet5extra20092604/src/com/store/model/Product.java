package com.store.model;

import com.store.exception.StoreOperationException;

public class Product {
    private final String id;
    private final String name;
    private double unitPrice;
    private final Category category;
    private boolean active;
    private int stock;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null) {
            throw new StoreOperationException("Product id cannot be null");
        }
        if (name == null) {
            throw new StoreOperationException("Product name cannot be null");
        }
        if (category == null) {
            throw new StoreOperationException("Product category cannot be null");
        }
        if (unitPrice < 0) {
            throw new StoreOperationException("Unit price cannot be negative");
        }
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
        this.stock = 0;
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

    public int getStock() {
        return stock;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new StoreOperationException("Quantity to increase stock must be positive");
        }
        this.stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new StoreOperationException("Quantity to decrease stock must be positive");
        }
        if (this.stock - quantity < 0) {
            throw new StoreOperationException("Stock cannot become negative for product " + id);
        }
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return id + " - " + name + " ($" + unitPrice + ") [" + category.getName() + "] active=" + active + " stock=" + stock;
    }
}