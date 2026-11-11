package com.store.repository;

import com.store.model.Order;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class OrderRepository {
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public boolean existsById(String id) {
        return orders.containsKey(id);
    }

    public Collection<Order> findAll() {
        return orders.values();
    }
}