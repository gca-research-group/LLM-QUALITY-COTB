package store.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String id;
    private Customer customer;
    private List<CartItem> items;

    public Cart(String id, Customer customer) {
        if (id == null || id.length() == 0 || customer == null) {
            throw new IllegalArgumentException("Invalid cart");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<CartItem>();
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<CartItem> getItems() {
        return new ArrayList<CartItem>(items);
    }

    public CartItem findItem(String productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equals(productId)) {
                return items.get(i);
            }
        }
        return null;
    }

    public void add(Product product, int quantity) {
        CartItem item = findItem(product.getId());

        if (item == null) {
            items.add(new CartItem(product, quantity));
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
    }

    public void update(String productId, int quantity) {
        CartItem item = findItem(productId);

        if (item == null) {
            throw new IllegalArgumentException("Product not in cart");
        }

        item.setQuantity(quantity);
    }

    public void remove(String productId) {
        CartItem item = findItem(productId);

        if (item == null) {
            throw new IllegalArgumentException("Product not in cart");
        }

        items.remove(item);
    }

    public double getSubtotal() {
        double total = 0;

        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getSubtotal();
        }

        return total;
    }
}