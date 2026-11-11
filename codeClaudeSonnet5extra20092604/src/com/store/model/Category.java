package com.store.model;

import com.store.exception.StoreOperationException;

public class Category {
    private final String id;
    private final String name;

    public Category(String id, String name) {
        if (id == null) {
            throw new StoreOperationException("Category id cannot be null");
        }
        if (name == null) {
            throw new StoreOperationException("Category name cannot be null");
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

    @Override
    public String toString() {
        return id + " (" + name + ")";
    }
}