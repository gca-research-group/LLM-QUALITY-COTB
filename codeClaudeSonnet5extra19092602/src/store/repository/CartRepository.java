package store.repository;

import store.model.Cart;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartRepository {
    private final Map<String, Cart> carts = new LinkedHashMap<>();

    public void add(Cart cart) {
        carts.put(cart.getId(), cart);
    }

    public Cart findById(String id) {
        return carts.get(id);
    }
}