package store.repository;

import store.exception.DuplicateIdException;
import store.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class InMemoryRepository<T> {
    private final Map<String, T> storage = new LinkedHashMap<>();
    private final String entityName;

    protected InMemoryRepository(String entityName) {
        this.entityName = entityName;
    }

    protected abstract String extractId(T entity);

    public void add(T entity) {
        String id = extractId(entity);
        if (storage.containsKey(id)) {
            throw new DuplicateIdException("Duplicate " + entityName + " id: " + id);
        }
        storage.put(id, entity);
    }

    public T findById(String id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(entityName + " not found with id: " + id);
        }
        return entity;
    }

    public boolean exists(String id) {
        return storage.containsKey(id);
    }

    public Collection<T> findAll() {
        return storage.values();
    }
}