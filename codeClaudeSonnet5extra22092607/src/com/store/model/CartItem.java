package com.store.model;

import com.store.exception.InvalidQuantityException;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Cart item quantity must be positive");
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

    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new InvalidQuantityException("Cart item quantity increase must be positive");
        }
        quantity += amount;
    }

    public void setQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new InvalidQuantityException("Cart item quantity must be positive");
        }
        quantity = newQuantity;
    }

    public double getSubtotal() {
        return quantity * product.getUnitPrice();
    }
}