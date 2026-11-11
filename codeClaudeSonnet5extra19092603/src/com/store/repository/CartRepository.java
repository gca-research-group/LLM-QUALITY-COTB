package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.model.Cart;

public class CartRepository {
    private final Map<String, Cart> carts = new LinkedHashMap<>();

    public void add(Cart cart) {
        carts.put(cart.getId(), cart);
    }

    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }

    public Collection<Cart> findAll() {
        return carts.values();
    }
}