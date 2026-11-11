package store.model;

import java.util.*;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String id, Customer customer) {
        if (id == null || id.isBlank() || customer == null) throw new IllegalArgumentException("Invalid cart");
        this.id = id;
        this.customer = customer;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }

    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public void add(Product product, int quantity) {
        if (product == null || !product.isActive()) throw new IllegalArgumentException("Inactive or invalid product");
        if (quantity <= 0) throw new IllegalArgumentException("Cart quantity must be positive");

        CartItem item = items.get(product.getId());
        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void update(String productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Cart quantity must be positive");

        CartItem item = items.get(productId);
        if (item == null) throw new IllegalArgumentException("Product not in cart");

        item.setQuantity(quantity);
    }

    public void remove(String productId) {
        if (items.remove(productId) == null) throw new IllegalArgumentException("Product not in cart");
    }

    public double getSubtotal() {
        return items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
    }
}