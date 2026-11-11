package com.store.repository;

import com.store.model.Product;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();

    public void save(Product product) {
        products.put(product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    public Collection<Product> findAll() {
        return products.values();
    }
}