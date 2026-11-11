package store.service;

import store.model.Customer;
import store.model.Supplier;
import store.repository.InMemoryRepository;

public class CustomerService {
    private final InMemoryRepository<Customer> customers =
        new InMemoryRepository<Customer>(new InMemoryRepository.IdProvider<Customer>() {
            public String getId(Customer value) {
                return value.getId();
            }
        });

    private final InMemoryRepository<Supplier> suppliers =
        new InMemoryRepository<Supplier>(new InMemoryRepository.IdProvider<Supplier>() {
            public String getId(Supplier value) {
                return value.getId();
            }
        });

    public void registerCustomer(Customer customer) {
        customers.save(customer);
    }

    public void registerSupplier(Supplier supplier) {
        suppliers.save(supplier);
    }

    public Customer findCustomer(String id) {
        return customers.findById(id);
    }

    public Supplier findSupplier(String id) {
        return suppliers.findById(id);
    }

    public int customerCount() {
        return customers.count();
    }

    public int supplierCount() {
        return suppliers.count();
    }
}