package com.store.repository;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<String, Customer>();

    public void add(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            throw new StoreOperationException("Duplicate customer id: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    public List<Customer> findAll() {
        return new ArrayList<Customer>(customers.values());
    }

    public int count() {
        return customers.size();
    }
}