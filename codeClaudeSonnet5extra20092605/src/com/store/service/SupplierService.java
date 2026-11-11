package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Supplier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupplierService {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();

    public void registerSupplier(Supplier supplier) {
        if (suppliers.containsKey(supplier.getId())) {
            throw new BusinessRuleException("Duplicate supplier identifier: " + supplier.getId());
        }
        suppliers.put(supplier.getId(), supplier);
    }

    public Supplier findById(String supplierId) {
        Supplier supplier = suppliers.get(supplierId);
        if (supplier == null) {
            throw new BusinessRuleException("Supplier not found: " + supplierId);
        }
        return supplier;
    }

    public List<Supplier> allSuppliers() {
        return new ArrayList<>(suppliers.values());
    }

    public int countSuppliers() {
        return suppliers.size();
    }
}