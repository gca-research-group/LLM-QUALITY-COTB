package com.store.repository;

import com.store.model.Category;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CategoryRepository {
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public void save(Category category) {
        categories.put(category.getId(), category);
    }

    public Optional<Category> findById(String id) {
        return Optional.ofNullable(categories.get(id));
    }

    public boolean existsById(String id) {
        return categories.containsKey(id);
    }

    public Collection<Category> findAll() {
        return categories.values();
    }
}