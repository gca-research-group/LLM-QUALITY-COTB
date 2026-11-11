package store.repository;

import store.exception.BusinessRuleException;
import store.model.Customer;

import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerRepository {
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void add(Customer customer) {
        if (customers.containsKey(customer.getId())) {
            throw new BusinessRuleException("Duplicate customer identifier: " + customer.getId());
        }
        customers.put(customer.getId(), customer);
    }

    public Customer findById(String id) {
        return customers.get(id);
    }

    public int count() {
        return customers.size();
    }
}