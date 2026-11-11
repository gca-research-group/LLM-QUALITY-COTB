package store.model;

import java.util.*;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final List<ReturnItem> items = new ArrayList<>();
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        if (id == null || id.isBlank() || order == null) {
            throw new IllegalArgumentException("Invalid return");
        }

        this.id = id;
        this.order = order;
    }

    public void addItem(Product product, int quantity) {
        if (processed) throw new IllegalStateException("Return already processed");

        int bought = order.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .mapToInt(OrderItem::getQuantity)
                .sum();

        int already = items.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .mapToInt(ReturnItem::getQuantity)
                .sum();

        if (quantity <= 0 || already + quantity > bought) {
            throw new IllegalArgumentException("Returned quantity exceeds quantity bought");
        }

        items.add(new ReturnItem(product, quantity));
    }

    public double getRefundTotal() {
        return items.stream().mapToDouble(i -> {
            double price = order.getItems().stream()
                    .filter(o -> o.getProduct().getId().equals(i.getProduct().getId()))
                    .mapToDouble(OrderItem::getUnitPrice)
                    .findFirst()
                    .orElse(0);

            return price * i.getQuantity();
        }).sum();
    }

    public String getId() { return id; }
    public Order getOrder() { return order; }

    public List<ReturnItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isProcessed() { return processed; }

    public void markProcessed() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Return must contain at least one item");
        }
        processed = true;
    }
}