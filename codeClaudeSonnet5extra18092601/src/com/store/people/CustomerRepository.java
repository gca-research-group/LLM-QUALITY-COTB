package com.store.people;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void register(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (customers.containsKey(customer.getId())) {
            throw new IllegalArgumentException("Duplicate customer id: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public Customer findById(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        return customer;
    }

    public List<Customer> all() {
        return new ArrayList<>(customers.values());
    }

    public int count() {
        return customers.size();
    }
}