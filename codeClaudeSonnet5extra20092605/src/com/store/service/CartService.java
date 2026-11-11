package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Cart;
import com.store.model.CartItem;
import com.store.model.Customer;
import com.store.model.Product;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CartService {
    private final Map<String, Cart> carts = new LinkedHashMap<>();

    public Cart createCart(String cartId, Customer customer) {
        if (carts.containsKey(cartId)) {
            throw new BusinessRuleException("Duplicate cart identifier: " + cartId);
        }
        Cart cart = new Cart(cartId, customer);
        carts.put(cartId, cart);
        return cart;
    }

    public Cart findById(String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new BusinessRuleException("Cart not found: " + cartId);
        }
        return cart;
    }

    public void addProduct(String cartId, Product product, int quantity) {
        if (!product.isActive()) {
            throw new BusinessRuleException("Inactive product cannot be added to cart: " + product.getId());
        }
        if (quantity <= 0) {
            throw new BusinessRuleException("Cart item quantity must be positive");
        }
        Cart cart = findById(cartId);
        Optional<CartItem> existing = cart.findItem(product.getId());
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            cart.addItem(new CartItem(product, quantity));
        }
    }

    public void updateQuantity(String cartId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Cart item quantity must be positive");
        }
        Cart cart = findById(cartId);
        CartItem item = cart.findItem(productId)
                .orElseThrow(() -> new BusinessRuleException("Product not present in cart: " + productId));
        item.setQuantity(quantity);
    }

    public void removeProduct(String cartId, String productId) {
        Cart cart = findById(cartId);
        cart.removeItem(productId);
    }

    public double subtotal(String cartId) {
        return findById(cartId).subtotal();
    }
}