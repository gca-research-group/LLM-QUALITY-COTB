package com.store.people;

public class Supplier {
    private final String id;
    private final String name;

    public Supplier(String id, String name) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Supplier id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty");
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