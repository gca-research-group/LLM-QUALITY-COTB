package store.service;

import store.model.Product;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stock = new LinkedHashMap<>();

    public void initialize(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        stock.put(product.getId(), quantity);
    }

    public void increase(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        stock.put(product.getId(), get(product) + quantity);
    }

    public void decrease(Product product, int quantity) {
        if (quantity < 0 || get(product) < quantity) {
            throw new IllegalStateException("Insufficient stock or invalid quantity");
        }

        stock.put(product.getId(), get(product) - quantity);
    }

    public int get(Product product) {
        return stock.getOrDefault(product.getId(), 0);
    }

    public Map<String, Integer> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(stock));
    }
}