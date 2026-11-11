package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.exception.DuplicateIdentifierException;
import com.store.model.Category;

public class CategoryRepository {
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public void add(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new DuplicateIdentifierException("Duplicate category identifier: " + category.getId());
        }
        categories.put(category.getId(), category);
    }

    public Optional<Category> findById(String id) {
        return Optional.ofNullable(categories.get(id));
    }

    public Collection<Category> findAll() {
        return categories.values();
    }
}