package store.repository;

import store.model.CashflowEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CashflowRepository {
    private final Map<String, CashflowEntry> entries = new LinkedHashMap<>();

    public void add(CashflowEntry entry) {
        entries.put(entry.getId(), entry);
    }

    public List<CashflowEntry> findAll() {
        return new ArrayList<>(entries.values());
    }
}