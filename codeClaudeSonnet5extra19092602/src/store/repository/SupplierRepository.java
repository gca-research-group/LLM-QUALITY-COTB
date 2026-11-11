package store.repository;

import store.exception.BusinessRuleException;
import store.model.Supplier;

import java.util.LinkedHashMap;
import java.util.Map;

public class SupplierRepository {
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();

    public void add(Supplier supplier) {
        if (suppliers.containsKey(supplier.getId())) {
            throw new BusinessRuleException("Duplicate supplier identifier: " + supplier.getId());
        }
        suppliers.put(supplier.getId(), supplier);
    }

    public Supplier findById(String id) {
        return suppliers.get(id);
    }

    public int count() {
        return suppliers.size();
    }
}