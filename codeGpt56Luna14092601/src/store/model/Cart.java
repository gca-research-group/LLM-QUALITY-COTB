package store.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Cart {
    private final String id;
    private final Customer customer;
    private final Map<String, Integer> items = new LinkedHashMap<>();

    public Cart(String id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Map<String, Integer> getItems() { return new LinkedHashMap<>(items); }

    public void put(String productId, int quantity) {
        items.put(productId, quantity);
    }

    public void remove(String productId) {
        items.remove(productId);
    }

    public double subtotal(Function<String, Product> lookup) {
        double total = 0;
        for (var entry : items.entrySet()) {
            total += lookup.apply(entry.getKey()).getUnitPrice() * entry.getValue();
        }
        return total;
    }
}