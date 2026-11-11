package com.store.cart;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart {
    private final String id;
    private final Customer customer;
    private final List<CartItem> items = new ArrayList<CartItem>();

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return new ArrayList<CartItem>(items);
    }

    public void addProduct(Product product, int quantity) {
        if (!product.isActive()) {
            throw new StoreOperationException("Cannot add inactive product to cart: " + product.getId());
        }
        if (quantity <= 0) {
            throw new StoreOperationException("Cart item quantity must be positive");
        }
        Optional<CartItem> existing = findItem(product.getId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void updateQuantity(String productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new StoreOperationException("Cart item quantity must be positive");
        }
        CartItem item = findItem(productId)
                .orElseThrow(() -> new StoreOperationException("Product not found in cart: " + productId));
        item.setQuantity(newQuantity);
    }

    public void removeProduct(String productId) {
        boolean removed = items.removeIf(i -> i.getProduct().getId().equals(productId));
        if (!removed) {
            throw new StoreOperationException("Product not found in cart: " + productId);
        }
    }

    private Optional<CartItem> findItem(String productId) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public double calculateSubtotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}