package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EntityNotFoundException;
import com.store.model.Supplier;
import com.store.repository.SupplierRepository;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void registerSupplier(Supplier supplier) {
        if (supplierRepository.existsById(supplier.getId())) {
            throw new DuplicateIdException("Duplicate supplier id: " + supplier.getId());
        }
        supplierRepository.save(supplier);
    }

    public Supplier findById(String supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + supplierId));
    }

    public int countSuppliers() {
        return supplierRepository.findAll().size();
    }
}