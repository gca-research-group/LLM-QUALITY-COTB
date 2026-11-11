package com.store.repository;

import com.store.cart.Cart;
import com.store.exception.StoreOperationException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CartRepository {
    private final Map<String, Cart> carts = new LinkedHashMap<String, Cart>();

    public void add(Cart cart) {
        if (carts.containsKey(cart.getId())) {
            throw new StoreOperationException("Duplicate cart id: " + cart.getId());
        }
        carts.put(cart.getId(), cart);
    }

    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }
}