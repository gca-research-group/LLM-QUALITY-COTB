package store.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Repository<T> {
    private final Map<String, T> data = new LinkedHashMap<>();

    public void add(String id, T value) {
        if (data.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate identifier: " + id);
        }

        data.put(id, value);
    }

    public T get(String id) {
        return data.get(id);
    }

    public Collection<T> all() {
        return Collections.unmodifiableCollection(data.values());
    }

    public int size() {
        return data.size();
    }
}