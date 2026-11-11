package com.store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart {
    private final String id;
    private final Customer customer;
    private final List<CartItem> items = new ArrayList<>();

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
        return new ArrayList<>(items);
    }

    public Optional<CartItem> findItem(String productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public double subtotal() {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}