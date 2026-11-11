package com.store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.store.exception.InvalidOperationException;
import com.store.exception.NotFoundException;

public class Cart {
    private final String id;
    private final Customer customer;
    private final List<CartItem> items;

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public void addProduct(Product product, int quantity) {
        if (!product.isActive()) {
            throw new InvalidOperationException("Inactive products cannot be added to carts: " + product.getId());
        }
        if (quantity <= 0) {
            throw new InvalidOperationException("Cart item quantity must be positive");
        }
        Optional<CartItem> existing = findItem(product);
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void updateQuantity(Product product, int quantity) {
        CartItem item = findItem(product)
                .orElseThrow(() -> new NotFoundException("Product not in cart: " + product.getId()));
        item.setQuantity(quantity);
    }

    public void removeProduct(Product product) {
        items.removeIf(item -> item.getProduct().getId().equals(product.getId()));
    }

    private Optional<CartItem> findItem(Product product) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
    }

    public double calculateSubtotal() {
        double sum = 0.0;
        for (CartItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}