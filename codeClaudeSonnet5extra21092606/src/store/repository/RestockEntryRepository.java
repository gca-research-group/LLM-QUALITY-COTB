package store.repository;

import store.model.RestockEntry;

public class RestockEntryRepository extends InMemoryRepository<RestockEntry> {

    public RestockEntryRepository() {
        super("RestockEntry");
    }

    @Override
    protected String extractId(RestockEntry entity) {
        return entity.getId();
    }
}