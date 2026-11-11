package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.exception.DuplicateIdentifierException;
import com.store.model.Supplier;

public class SupplierRepository {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();

    public void add(Supplier supplier) {
        if (suppliers.containsKey(supplier.getId())) {
            throw new DuplicateIdentifierException("Duplicate supplier identifier: " + supplier.getId());
        }
        suppliers.put(supplier.getId(), supplier);
    }

    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public Collection<Supplier> findAll() {
        return suppliers.values();
    }
}