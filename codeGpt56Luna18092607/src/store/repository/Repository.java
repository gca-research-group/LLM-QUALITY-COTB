package store.repository;

import java.util.List;

public interface Repository<T> {
    void save(T value);
    T findById(String id);
    List<T> findAll();
}