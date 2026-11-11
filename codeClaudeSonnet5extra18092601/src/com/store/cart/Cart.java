package com.store.cart;

import com.store.catalog.Product;
import com.store.people.Customer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String id, Customer customer) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Cart id cannot be null or empty");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.id = id;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (!product.isActive()) {
            throw new IllegalStateException("Inactive products cannot be added to carts: " + product.getId());
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart item quantity must be positive");
        }
        CartItem existing = items.get(product.getId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(product.getId(), new CartItem(product, quantity));
        }
    }

    public void updateQuantity(String productId, int quantity) {
        CartItem item = items.get(productId);
        if (item == null) {
            throw new IllegalArgumentException("Product not in cart: " + productId);
        }
        item.setQuantity(quantity);
    }

    public void removeProduct(String productId) {
        if (!items.containsKey(productId)) {
            throw new IllegalArgumentException("Product not in cart: " + productId);
        }
        items.remove(productId);
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public double getSubtotal() {
        double total = 0.0;
        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}