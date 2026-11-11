package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;
import com.store.repository.CustomerRepository;

import java.util.List;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        customerRepository.add(customer);
    }

    public Customer getCustomerOrThrow(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new StoreOperationException("Customer not found: " + id));
    }

    public List<Customer> listAll() {
        return customerRepository.findAll();
    }

    public int count() {
        return customerRepository.count();
    }
}