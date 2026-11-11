package com.store.repository;

import com.store.model.Customer;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    public boolean existsById(String id) {
        return customers.containsKey(id);
    }

    public Collection<Customer> findAll() {
        return customers.values();
    }
}