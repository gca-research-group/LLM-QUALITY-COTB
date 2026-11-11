package com.store.service;

import java.util.Collection;

import com.store.exception.NotFoundException;
import com.store.model.Customer;
import com.store.repository.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        customerRepository.add(customer);
    }

    public Customer requireCustomer(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    public Collection<Customer> findAll() {
        return customerRepository.findAll();
    }
}