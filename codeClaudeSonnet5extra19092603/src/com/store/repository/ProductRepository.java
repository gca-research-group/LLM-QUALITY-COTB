package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.exception.DuplicateIdentifierException;
import com.store.model.Product;

public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();

    public void add(Product product) {
        if (products.containsKey(product.getId())) {
            throw new DuplicateIdentifierException("Duplicate product identifier: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public Collection<Product> findAll() {
        return products.values();
    }
}