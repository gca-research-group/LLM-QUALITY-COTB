package store.model;

import store.exception.BusinessRuleException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items;

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new LinkedHashMap<>();
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public void addProduct(Product product, int quantity) {
        if (!product.isActive()) {
            throw new BusinessRuleException("Inactive product cannot be added to cart: " + product.getId());
        }
        if (quantity <= 0) {
            throw new BusinessRuleException("Cart item quantity must be positive for product " + product.getId());
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
            throw new BusinessRuleException("Product not found in cart: " + productId);
        }
        item.setQuantity(quantity);
    }

    public void removeProduct(String productId) {
        if (!items.containsKey(productId)) {
            throw new BusinessRuleException("Product not found in cart: " + productId);
        }
        items.remove(productId);
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items.values()) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}