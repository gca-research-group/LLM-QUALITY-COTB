package store.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import store.model.Cart;
import store.model.Customer;
import store.model.Product;

public class CartService {
    private final Map<String, Cart> carts = new LinkedHashMap<String, Cart>();
    private final CatalogService catalog;

    public CartService(CatalogService catalog) {
        this.catalog = catalog;
    }

    public Cart createCart(String id, Customer customer) {
        if (carts.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate cart");
        }

        Cart cart = new Cart(id, customer);
        carts.put(id, cart);
        return cart;
    }

    public Cart getCart(String id) {
        Cart cart = carts.get(id);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        return cart;
    }

    public void add(String cartId, String productId, int quantity) {
        Product product = catalog.require(productId);
        getCart(cartId).addProduct(product, quantity);
    }

    public void update(String cartId, String productId, int quantity) {
        getCart(cartId).updateQuantity(productId, quantity);
    }

    public void remove(String cartId, String productId) {
        getCart(cartId).removeProduct(productId);
    }

    public double subtotal(String cartId) {
        return getCart(cartId).getSubtotal();
    }

    public Collection<Cart> allCarts() {
        return carts.values();
    }
}