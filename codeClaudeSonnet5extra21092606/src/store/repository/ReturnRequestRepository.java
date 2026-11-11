package store.repository;

import store.model.ReturnRequest;

public class ReturnRequestRepository extends InMemoryRepository<ReturnRequest> {

    public ReturnRequestRepository() {
        super("ReturnRequest");
    }

    @Override
    protected String extractId(ReturnRequest entity) {
        return entity.getId();
    }
}