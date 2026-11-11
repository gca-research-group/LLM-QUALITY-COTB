package store.repository;

import store.model.Customer;

public class CustomerRepository extends InMemoryRepository<Customer> {

    public CustomerRepository() {
        super("Customer");
    }

    @Override
    protected String extractId(Customer entity) {
        return entity.getId();
    }
}