package com.store.repository;

import com.store.model.ReturnRequest;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ReturnRequestRepository {
    private final Map<String, ReturnRequest> returnRequests = new LinkedHashMap<>();

    public void save(ReturnRequest returnRequest) {
        returnRequests.put(returnRequest.getId(), returnRequest);
    }

    public Optional<ReturnRequest> findById(String id) {
        return Optional.ofNullable(returnRequests.get(id));
    }

    public boolean existsById(String id) {
        return returnRequests.containsKey(id);
    }

    public Collection<ReturnRequest> findAll() {
        return returnRequests.values();
    }
}