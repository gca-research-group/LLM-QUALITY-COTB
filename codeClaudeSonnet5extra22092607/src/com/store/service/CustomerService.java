package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EntityNotFoundException;
import com.store.model.Customer;
import com.store.repository.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        if (customerRepository.existsById(customer.getId())) {
            throw new DuplicateIdException("Duplicate customer id: " + customer.getId());
        }
        customerRepository.save(customer);
    }

    public Customer findById(String customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));
    }

    public int countCustomers() {
        return customerRepository.findAll().size();
    }
}