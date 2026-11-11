package store.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<T> implements Repository<T> {
    private Map<String, T> data;

    public InMemoryRepository() {
        data = new LinkedHashMap<String, T>();
    }

    public void save(T value) {
        throw new UnsupportedOperationException("Use save with identifier");
    }

    public void save(String id, T value) {
        if (data.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate identifier");
        }

        data.put(id, value);
    }

    public T findById(String id) {
        return data.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<T>(data.values());
    }
}