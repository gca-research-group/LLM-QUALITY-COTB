package com.store.model;

import com.store.exception.EntityNotFoundException;
import com.store.exception.InactiveProductException;
import com.store.exception.InvalidQuantityException;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final String customerId;
    private final Map<String, CartItem> items;

    public Cart(String id, String customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new LinkedHashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void addItem(Product product, int quantity) {
        if (!product.isActive()) {
            throw new InactiveProductException("Inactive product cannot be added to cart: " + product.getId());
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Cart item quantity must be positive");
        }
        CartItem existing = items.get(product.getId());
        if (existing != null) {
            existing.increaseQuantity(quantity);
        } else {
            items.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void updateQuantity(String productId, int newQuantity) {
        CartItem item = items.get(productId);
        if (item == null) {
            throw new EntityNotFoundException("Product not present in cart: " + productId);
        }
        if (newQuantity <= 0) {
            throw new InvalidQuantityException("Cart item quantity must be positive");
        }
        item.setQuantity(newQuantity);
    }

    public void removeItem(String productId) {
        if (items.remove(productId) == null) {
            throw new EntityNotFoundException("Product not present in cart: " + productId);
        }
    }

    public double getSubtotal() {
        double total = 0.0;
        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public void clear() {
        items.clear();
    }
}