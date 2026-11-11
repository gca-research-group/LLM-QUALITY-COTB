package store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryRepository<T> implements Repository<T> {
    public interface IdProvider<T> {
        String getId(T value);
    }

    private final Map<String, T> data = new LinkedHashMap<String, T>();
    private final IdProvider<T> idProvider;

    public InMemoryRepository(IdProvider<T> idProvider) {
        this.idProvider = idProvider;
    }

    public void save(T value) {
        String id = idProvider.getId(value);

        if (data.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate identifier: " + id);
        }

        data.put(id, value);
    }

    public T findById(String id) {
        return data.get(id);
    }

    public Collection<T> findAll() {
        return data.values();
    }

    public int count() {
        return data.size();
    }
}