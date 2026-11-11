package com.store.service;

import java.util.Collection;

import com.store.exception.NotFoundException;
import com.store.model.Supplier;
import com.store.repository.SupplierRepository;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void registerSupplier(Supplier supplier) {
        supplierRepository.add(supplier);
    }

    public Supplier requireSupplier(String id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found: " + id));
    }

    public Collection<Supplier> findAll() {
        return supplierRepository.findAll();
    }
}