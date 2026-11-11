package com.store.repository;

import com.store.model.Cart;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CartRepository {
    private final Map<String, Cart> carts = new LinkedHashMap<>();

    public void save(Cart cart) {
        carts.put(cart.getId(), cart);
    }

    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }

    public boolean existsById(String id) {
        return carts.containsKey(id);
    }

    public Collection<Cart> findAll() {
        return carts.values();
    }
}