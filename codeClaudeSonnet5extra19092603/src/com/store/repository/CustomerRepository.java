package com.store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.store.exception.DuplicateIdentifierException;
import com.store.model.Customer;

public class CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void add(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            throw new DuplicateIdentifierException("Duplicate customer identifier: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    public Collection<Customer> findAll() {
        return customers.values();
    }
}