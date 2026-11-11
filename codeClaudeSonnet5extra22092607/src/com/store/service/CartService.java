package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EntityNotFoundException;
import com.store.model.Cart;
import com.store.model.Product;
import com.store.repository.CartRepository;
import com.store.repository.ProductRepository;

public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Cart createCart(String cartId, String customerId) {
        if (cartRepository.existsById(cartId)) {
            throw new DuplicateIdException("Duplicate cart id: " + cartId);
        }
        Cart cart = new Cart(cartId, customerId);
        cartRepository.save(cart);
        return cart;
    }

    public void addItemToCart(String cartId, String productId, int quantity) {
        Cart cart = getCart(cartId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        cart.addItem(product, quantity);
    }

    public void updateCartItemQuantity(String cartId, String productId, int newQuantity) {
        Cart cart = getCart(cartId);
        cart.updateQuantity(productId, newQuantity);
    }

    public void removeCartItem(String cartId, String productId) {
        Cart cart = getCart(cartId);
        cart.removeItem(productId);
    }

    public double getCartSubtotal(String cartId) {
        return getCart(cartId).getSubtotal();
    }

    public Cart getCart(String cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found: " + cartId));
    }
}