package store.model;

import store.exception.InsufficientStockException;
import store.exception.InvalidAmountException;
import store.exception.InvalidQuantityException;

public class Product {
    private final String id;
    private final String name;
    private final double unitPrice;
    private final Category category;
    private boolean active;
    private int stockQuantity;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product id cannot be blank");
        }
        if (unitPrice < 0) {
            throw new InvalidAmountException("Unit price cannot be negative for product " + id);
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
            throw new InvalidQuantityException("Quantity to increase must be positive for product " + id);
        }
        this.stockQuantity += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity to decrease must be positive for product " + id);
        }
        if (this.stockQuantity - quantity < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for product " + id + ": available " + stockQuantity + ", requested " + quantity);
        }
        this.stockQuantity -= quantity;
    }
}