package store.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
    public static class CartItem {
        private final Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double subtotal() {
            return product.getUnitPrice() * quantity;
        }
    }

    private final String id;
    private final Customer customer;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String id, Customer customer) {
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
        return Collections.unmodifiableCollection(items.values());
    }

    public void add(Product product, int quantity) {
        if (!product.isActive()) {
            throw new IllegalStateException("Inactive product cannot be added");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive");
        }

        CartItem item = items.get(product.getId());

        if (item == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void update(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive");
        }

        CartItem item = items.get(productId);

        if (item == null) {
            throw new IllegalArgumentException("Product is not in cart");
        }

        item.setQuantity(quantity);
    }

    public void remove(String productId) {
        items.remove(productId);
    }

    public double subtotal() {
        return items.values().stream()
                .mapToDouble(CartItem::subtotal)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}