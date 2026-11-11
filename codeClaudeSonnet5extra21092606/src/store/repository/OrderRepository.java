package store.repository;

import store.model.Order;

public class OrderRepository extends InMemoryRepository<Order> {

    public OrderRepository() {
        super("Order");
    }

    @Override
    protected String extractId(Order entity) {
        return entity.getId();
    }
}