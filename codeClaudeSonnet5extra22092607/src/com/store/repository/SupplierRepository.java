package com.store.repository;

import com.store.model.Supplier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SupplierRepository {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();

    public void save(Supplier supplier) {
        suppliers.put(supplier.getId(), supplier);
    }

    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public boolean existsById(String id) {
        return suppliers.containsKey(id);
    }

    public Collection<Supplier> findAll() {
        return suppliers.values();
    }
}