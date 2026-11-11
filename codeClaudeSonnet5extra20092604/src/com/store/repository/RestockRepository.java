package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.restock.RestockOperation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestockRepository {
    private final Map<String, RestockOperation> operations = new LinkedHashMap<String, RestockOperation>();

    public void add(RestockOperation operation) {
        if (operations.containsKey(operation.getId())) {
            throw new StoreOperationException("Duplicate restock id: " + operation.getId());
        }
        operations.put(operation.getId(), operation);
    }

    public List<RestockOperation> findAll() {
        return new ArrayList<RestockOperation>(operations.values());
    }
}