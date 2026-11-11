package store.repository;

import store.exception.BusinessRuleException;
import store.model.Category;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoryRepository {
    private final Map<String, Category> categories = new LinkedHashMap<>();

    public void add(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new BusinessRuleException("Duplicate category identifier: " + category.getId());
        }
        categories.put(category.getId(), category);
    }

    public Category findById(String id) {
        return categories.get(id);
    }

    public boolean exists(String id) {
        return categories.containsKey(id);
    }
}