package com.store.cart;

import com.store.catalog.Catalog;
import com.store.catalog.Product;
import com.store.people.Customer;
import com.store.people.CustomerRepository;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartService {
    private final Catalog catalog;
    private final CustomerRepository customerRepository;
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private int cartSequence = 0;

    public CartService(Catalog catalog, CustomerRepository customerRepository) {
        this.catalog = catalog;
        this.customerRepository = customerRepository;
    }

    public Cart createCart(String customerId) {
        Customer customer = customerRepository.findById(customerId);
        cartSequence++;
        String cartId = "CART" + cartSequence;
        Cart cart = new Cart(cartId, customer);
        carts.put(cartId, cart);
        return cart;
    }

    public Cart findById(String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found: " + cartId);
        }
        return cart;
    }

    public void addProduct(String cartId, String productId, int quantity) {
        Cart cart = findById(cartId);
        Product product = catalog.findById(productId);
        cart.addProduct(product, quantity);
    }

    public void updateQuantity(String cartId, String productId, int quantity) {
        findById(cartId).updateQuantity(productId, quantity);
    }

    public void removeProduct(String cartId, String productId) {
        findById(cartId).removeProduct(productId);
    }

    public double getSubtotal(String cartId) {
        return findById(cartId).getSubtotal();
    }
}