package com.store.service;

import com.store.cart.Cart;
import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.model.Product;
import com.store.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;
    private final CustomerService customerService;
    private final CatalogService catalogService;

    public CartService(CartRepository cartRepository, CustomerService customerService, CatalogService catalogService) {
        this.cartRepository = cartRepository;
        this.customerService = customerService;
        this.catalogService = catalogService;
    }

    public Cart createCart(String cartId, String customerId) {
        Customer customer = customerService.getCustomerOrThrow(customerId);
        Cart cart = new Cart(cartId, customer);
        cartRepository.add(cart);
        return cart;
    }

    public Cart getCartOrThrow(String cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new StoreOperationException("Cart not found: " + cartId));
    }

    public void addProductToCart(String cartId, String productId, int quantity) {
        Cart cart = getCartOrThrow(cartId);
        Product product = catalogService.getProductOrThrow(productId);
        cart.addProduct(product, quantity);
    }

    public void updateCartItemQuantity(String cartId, String productId, int quantity) {
        Cart cart = getCartOrThrow(cartId);
        cart.updateQuantity(productId, quantity);
    }

    public void removeCartItem(String cartId, String productId) {
        Cart cart = getCartOrThrow(cartId);
        cart.removeProduct(productId);
    }

    public double getCartSubtotal(String cartId) {
        return getCartOrThrow(cartId).calculateSubtotal();
    }
}