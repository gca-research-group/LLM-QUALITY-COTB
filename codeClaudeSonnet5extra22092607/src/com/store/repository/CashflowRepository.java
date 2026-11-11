package com.store.repository;

import com.store.model.CashflowEntry;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CashflowRepository {
    private final Map<String, CashflowEntry> entries = new LinkedHashMap<>();

    public void save(CashflowEntry entry) {
        entries.put(entry.getId(), entry);
    }

    public Optional<CashflowEntry> findById(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public boolean existsById(String id) {
        return entries.containsKey(id);
    }

    public Collection<CashflowEntry> findAll() {
        return entries.values();
    }
}