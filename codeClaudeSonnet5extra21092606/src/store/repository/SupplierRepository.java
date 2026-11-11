package store.repository;

import store.model.Supplier;

public class SupplierRepository extends InMemoryRepository<Supplier> {

    public SupplierRepository() {
        super("Supplier");
    }

    @Override
    protected String extractId(Supplier entity) {
        return entity.getId();
    }
}