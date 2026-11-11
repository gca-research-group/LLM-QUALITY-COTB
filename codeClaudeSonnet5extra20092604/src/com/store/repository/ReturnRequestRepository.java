package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.returns.ReturnRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReturnRequestRepository {
    private final Map<String, ReturnRequest> returnRequests = new LinkedHashMap<String, ReturnRequest>();

    public void add(ReturnRequest returnRequest) {
        if (returnRequests.containsKey(returnRequest.getId())) {
            throw new StoreOperationException("Duplicate return id: " + returnRequest.getId());
        }
        returnRequests.put(returnRequest.getId(), returnRequest);
    }

    public Optional<ReturnRequest> findById(String id) {
        return Optional.ofNullable(returnRequests.get(id));
    }

    public List<ReturnRequest> findAll() {
        return new ArrayList<ReturnRequest>(returnRequests.values());
    }
}