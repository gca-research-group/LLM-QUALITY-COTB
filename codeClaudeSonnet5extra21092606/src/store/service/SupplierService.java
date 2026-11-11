package store.service;

import store.model.Supplier;
import store.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;

public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public void registerSupplier(Supplier supplier) {
        supplierRepository.add(supplier);
    }

    public Supplier findById(String supplierId) {
        return supplierRepository.findById(supplierId);
    }

    public List<Supplier> allSuppliers() {
        return new ArrayList<>(supplierRepository.findAll());
    }

    public int supplierCount() {
        return supplierRepository.findAll().size();
    }
}