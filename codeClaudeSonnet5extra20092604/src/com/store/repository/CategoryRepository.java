package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.model.Category;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CategoryRepository {
    private final Map<String, Category> categories = new LinkedHashMap<String, Category>();

    public void add(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new StoreOperationException("Duplicate category id: " + category.getId());
        }
        categories.put(category.getId(), category);
    }

    public Optional<Category> findById(String id) {
        return Optional.ofNullable(categories.get(id));
    }
}