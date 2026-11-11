package store.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<String, CartItem>();

    public Cart(String id, Customer customer) {
        if (id == null || customer == null) {
            throw new IllegalArgumentException("Invalid cart");
        }
        this.id = id;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public void add(Product product, int quantity) {
        if (product == null || !product.isActive() || quantity <= 0) {
            throw new IllegalArgumentException("Invalid cart addition");
        }

        CartItem item = items.get(product.getId());

        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void update(String productId, int quantity) {
        CartItem item = items.get(productId);

        if (item == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid cart update");
        }

        item.setQuantity(quantity);
    }

    public void remove(String productId) {
        if (items.remove(productId) == null) {
            throw new IllegalArgumentException("Product not in cart");
        }
    }

    public double getSubtotal() {
        double total = 0;

        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }

        return total;
    }
}