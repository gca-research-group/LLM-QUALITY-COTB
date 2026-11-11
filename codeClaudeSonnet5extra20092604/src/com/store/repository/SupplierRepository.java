package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.model.Supplier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SupplierRepository {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<String, Supplier>();

    public void add(Supplier supplier) {
        if (suppliers.containsKey(supplier.getId())) {
            throw new StoreOperationException("Duplicate supplier id: " + supplier.getId());
        }
        suppliers.put(supplier.getId(), supplier);
    }

    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public List<Supplier> findAll() {
        return new ArrayList<Supplier>(suppliers.values());
    }

    public int count() {
        return suppliers.size();
    }
}