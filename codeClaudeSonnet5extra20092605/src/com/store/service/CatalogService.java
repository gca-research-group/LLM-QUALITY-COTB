package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Category;
import com.store.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogService {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public void registerCategory(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new BusinessRuleException("Duplicate category identifier: " + category.getId());
        }
        categories.put(category.getId(), category);
    }

    public Category findCategoryById(String categoryId) {
        Category category = categories.get(categoryId);
        if (category == null) {
            throw new BusinessRuleException("Category not found: " + categoryId);
        }
        return category;
    }

    public void addProduct(Product product) {
        if (products.containsKey(product.getId())) {
            throw new BusinessRuleException("Duplicate product identifier: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    public Product findById(String productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new BusinessRuleException("Product not found: " + productId);
        }
        return product;
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getName().equalsIgnoreCase(name)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> findByCategory(String categoryId) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }
        return result;
    }

    public void activateProduct(String productId) {
        findById(productId).activate();
    }

    public void deactivateProduct(String productId) {
        findById(productId).deactivate();
    }

    public List<Product> allProducts() {
        return new ArrayList<>(products.values());
    }

    public int countProducts() {
        return products.size();
    }
}