package store.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<String, CartItem>();

    public Cart(String id, Customer customer) {
        if (id == null || id.length() == 0 || customer == null) {
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

    public void addProduct(Product product, int quantity) {
        if (product == null || !product.isActive()) {
            throw new IllegalArgumentException("Inactive or invalid product");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem item = items.get(product.getId());

        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        CartItem item = items.get(productId);

        if (item == null) {
            throw new IllegalArgumentException("Product not in cart");
        }

        item.setQuantity(quantity);
    }

    public void removeProduct(String productId) {
        items.remove(productId);
    }

    public Collection<CartItem> getItems() {
        return items.values();
    }

    public double getSubtotal() {
        double total = 0;

        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }

        return total;
    }
}