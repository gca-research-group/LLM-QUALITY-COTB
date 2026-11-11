package com.store.people;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupplierRepository {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();

    public void register(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        if (suppliers.containsKey(supplier.getId())) {
            throw new IllegalArgumentException("Duplicate supplier id: " + supplier.getId());
        }
        suppliers.put(supplier.getId(), supplier);
    }

    public Supplier findById(String supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found: " + supplierId);
        }
        return supplier;
    }

    public List<Supplier> all() {
        return new ArrayList<>(suppliers.values());
    }

    public int count() {
        return suppliers.size();
    }
}