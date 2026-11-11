package store.repository;

import store.model.CashflowEntry;

public class CashflowRepository extends InMemoryRepository<CashflowEntry> {

    public CashflowRepository() {
        super("CashflowEntry");
    }

    @Override
    protected String extractId(CashflowEntry entity) {
        return entity.getId();
    }
}