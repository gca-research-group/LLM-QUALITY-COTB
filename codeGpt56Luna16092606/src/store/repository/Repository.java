package store.repository;

import java.util.Collection;

public interface Repository<T> {
    void save(T value);
    T findById(String id);
    Collection<T> findAll();
    int count();
}