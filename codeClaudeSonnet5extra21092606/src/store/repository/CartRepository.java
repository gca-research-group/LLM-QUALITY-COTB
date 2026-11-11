package store.repository;

import store.model.Cart;

public class CartRepository extends InMemoryRepository<Cart> {

    public CartRepository() {
        super("Cart");
    }

    @Override
    protected String extractId(Cart entity) {
        return entity.getId();
    }
}