package com.store.repository;

import com.store.model.RestockOperation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class RestockRepository {
    private final Map<String, RestockOperation> restocks = new LinkedHashMap<>();

    public void save(RestockOperation restockOperation) {
        restocks.put(restockOperation.getId(), restockOperation);
    }

    public Optional<RestockOperation> findById(String id) {
        return Optional.ofNullable(restocks.get(id));
    }

    public boolean existsById(String id) {
        return restocks.containsKey(id);
    }

    public Collection<RestockOperation> findAll() {
        return restocks.values();
    }
}