package com.store.catalog;

public class Product {
    private final String id;
    private String name;
    private double unitPrice;
    private Category category;
    private boolean active;
    private int stockQuantity;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Increase quantity must be positive");
        }
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Decrease quantity must be positive");
        }
        if (this.stockQuantity - quantity < 0) {
            throw new IllegalStateException("Stock cannot become negative for product " + id);
        }
        this.stockQuantity -= quantity;
    }
}