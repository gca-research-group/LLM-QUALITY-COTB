package store.model;

import store.exception.EntityNotFoundException;
import store.exception.InactiveProductException;
import store.exception.InvalidQuantityException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        return Collections.unmodifiableList(items);
    }

    public void addProduct(Product product, int quantity) {
        if (!product.isActive()) {
            throw new InactiveProductException("Cannot add inactive product to cart: " + product.getId());
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Cart item quantity must be positive");
        }
        Optional<CartItem> existing = findItem(product.getId());
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public void updateQuantity(String productId, int quantity) {
        CartItem item = findItem(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not in cart: " + productId));
        item.setQuantity(quantity);
    }

    public void removeProduct(String productId) {
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new EntityNotFoundException("Product not in cart: " + productId);
        }
    }

    private Optional<CartItem> findItem(String productId) {
        return items.stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst();
    }

    public double getSubtotal() {
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