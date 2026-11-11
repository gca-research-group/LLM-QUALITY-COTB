package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Customer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void registerCustomer(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            throw new BusinessRuleException("Duplicate customer identifier: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public Customer findById(String customerId) {
        Customer customer = customers.get(customerId);
        if (customer == null) {
            throw new BusinessRuleException("Customer not found: " + customerId);
        }
        return customer;
    }

    public List<Customer> allCustomers() {
        return new ArrayList<>(customers.values());
    }

    public int countCustomers() {
        return customers.size();
    }
}