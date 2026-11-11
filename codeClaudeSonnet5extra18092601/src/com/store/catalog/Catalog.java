package com.store.catalog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Catalog {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public void registerCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (categories.containsKey(category.getId())) {
            throw new IllegalArgumentException("Duplicate category id: " + category.getId());
        }
        categories.put(category.getId(), category);
    }

    public void registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (products.containsKey(product.getId())) {
            throw new IllegalArgumentException("Duplicate product id: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    public Product findById(String productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        return product;
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        if (name == null) {
            return result;
        }
        String lower = name.toLowerCase();
        for (Product p : products.values()) {
            if (p.getName().toLowerCase().contains(lower)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> listByCategory(String categoryId) {
        List<Product> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.getCategory().getId().equals(categoryId)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> allProducts() {
        return new ArrayList<>(products.values());
    }

    public int productCount() {
        return products.size();
    }

    public void activateProduct(String productId) {
        findById(productId).activate();
    }

    public void deactivateProduct(String productId) {
        findById(productId).deactivate();
    }
}