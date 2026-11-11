package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.model.ReturnRequest;

public class ReturnRequestRepository {
    private final Map<String, ReturnRequest> returnRequests = new LinkedHashMap<>();

    public void add(ReturnRequest returnRequest) {
        returnRequests.put(returnRequest.getId(), returnRequest);
    }

    public Optional<ReturnRequest> findById(String id) {
        return Optional.ofNullable(returnRequests.get(id));
    }

    public Collection<ReturnRequest> findAll() {
        return returnRequests.values();
    }
}