package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderRepository {
    private final Map<String, Order> orders = new LinkedHashMap<String, Order>();

    public void add(Order order) {
        if (orders.containsKey(order.getId())) {
            throw new StoreOperationException("Duplicate order id: " + order.getId());
        }
        orders.put(order.getId(), order);
    }

    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<Order>(orders.values());
    }
}