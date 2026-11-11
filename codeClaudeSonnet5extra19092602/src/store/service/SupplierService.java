package store.service;

import store.exception.BusinessRuleException;
import store.model.Supplier;
import store.repository.SupplierRepository;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void registerSupplier(Supplier supplier) {
        supplierRepository.add(supplier);
    }

    public Supplier findSupplier(String supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId);
        if (supplier == null) {
            throw new BusinessRuleException("Supplier not found: " + supplierId);
        }
        return supplier;
    }

    public int totalSupplierCount() {
        return supplierRepository.count();
    }
}