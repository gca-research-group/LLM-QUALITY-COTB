package com.store.catalog;

public class Category {
    private final String id;
    private final String name;

    public Category(String id, String name) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Category id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}