package store.repository;

import store.model.RestockEntry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestockRepository {
    private final Map<String, RestockEntry> entries = new LinkedHashMap<>();

    public void add(RestockEntry entry) {
        entries.put(entry.getId(), entry);
    }

    public List<RestockEntry> findAll() {
        return new ArrayList<>(entries.values());
    }
}