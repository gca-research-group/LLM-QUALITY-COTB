package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<String, Product>();

    public void add(Product product) {
        if (products.containsKey(product.getId())) {
            throw new StoreOperationException("Duplicate product id: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<Product>();
        for (Product p : products.values()) {
            if (p.getName().equalsIgnoreCase(name)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> findByCategory(String categoryId) {
        List<Product> result = new ArrayList<Product>();
        for (Product p : products.values()) {
            if (p.getCategory().getId().equals(categoryId)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> findAll() {
        return new ArrayList<Product>(products.values());
    }
}