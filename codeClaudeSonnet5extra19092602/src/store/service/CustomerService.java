package store.service;

import store.exception.BusinessRuleException;
import store.model.Customer;
import store.repository.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        customerRepository.add(customer);
    }

    public Customer findCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new BusinessRuleException("Customer not found: " + customerId);
        }
        return customer;
    }

    public int totalCustomerCount() {
        return customerRepository.count();
    }
}