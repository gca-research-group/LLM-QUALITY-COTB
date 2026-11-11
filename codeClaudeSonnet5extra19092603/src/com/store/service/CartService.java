package com.store.service;

import java.util.Collection;

import com.store.exception.NotFoundException;
import com.store.model.Cart;
import com.store.model.Customer;
import com.store.model.Product;
import com.store.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart(String id, Customer customer) {
        Cart cart = new Cart(id, customer);
        cartRepository.add(cart);
        return cart;
    }

    public Cart requireCart(String id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cart not found: " + id));
    }

    public void addProduct(String cartId, Product product, int quantity) {
        requireCart(cartId).addProduct(product, quantity);
    }

    public void updateQuantity(String cartId, Product product, int quantity) {
        requireCart(cartId).updateQuantity(product, quantity);
    }

    public void removeProduct(String cartId, Product product) {
        requireCart(cartId).removeProduct(product);
    }

    public double calculateSubtotal(String cartId) {
        return requireCart(cartId).calculateSubtotal();
    }

    public Collection<Cart> findAll() {
        return cartRepository.findAll();
    }
}