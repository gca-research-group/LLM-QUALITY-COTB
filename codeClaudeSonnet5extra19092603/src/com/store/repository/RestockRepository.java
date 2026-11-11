package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.store.model.RestockEntry;

public class RestockRepository {
    private final Map<String, RestockEntry> restockEntries = new LinkedHashMap<>();

    public void add(RestockEntry entry) {
        restockEntries.put(entry.getId(), entry);
    }

    public Collection<RestockEntry> findAll() {
        return restockEntries.values();
    }
}