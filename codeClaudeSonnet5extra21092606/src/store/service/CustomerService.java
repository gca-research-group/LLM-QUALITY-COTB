package store.service;

import store.model.Customer;
import store.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(Customer customer) {
        customerRepository.add(customer);
    }

    public Customer findById(String customerId) {
        return customerRepository.findById(customerId);
    }

    public List<Customer> allCustomers() {
        return new ArrayList<>(customerRepository.findAll());
    }

    public int customerCount() {
        return customerRepository.findAll().size();
    }
}