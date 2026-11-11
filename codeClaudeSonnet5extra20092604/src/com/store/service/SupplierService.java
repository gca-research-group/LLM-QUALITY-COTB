package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.model.Supplier;
import com.store.repository.SupplierRepository;

import java.util.List;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void registerSupplier(Supplier supplier) {
        supplierRepository.add(supplier);
    }

    public Supplier getSupplierOrThrow(String id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new StoreOperationException("Supplier not found: " + id));
    }

    public List<Supplier> listAll() {
        return supplierRepository.findAll();
    }

    public int count() {
        return supplierRepository.count();
    }
}