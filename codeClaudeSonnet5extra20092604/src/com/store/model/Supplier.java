package com.store.model;

import com.store.exception.StoreOperationException;

public class Supplier {
    private final String id;
    private final String name;

    public Supplier(String id, String name) {
        if (id == null) {
            throw new StoreOperationException("Supplier id cannot be null");
        }
        if (name == null) {
            throw new StoreOperationException("Supplier name cannot be null");
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
        return id + " - " + name;
    }
}