package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    public static class ReturnedItem {
        private final Product product;
        private final int quantity;
        private final double unitPrice;

        public ReturnedItem(Product product, int quantity, double unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double total() {
            return quantity * unitPrice;
        }
    }

    private final String id;
    private final Order order;
    private final List<ReturnedItem> items = new ArrayList<>();
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        if (order.getStatus() != Order.Status.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        this.id = id;
        this.order = order;
    }

    public void add(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Returned quantity must be positive");
        }

        int bought = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .mapToInt(Order.OrderItem::getQuantity)
                .sum();

        int existing = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .mapToInt(ReturnedItem::getQuantity)
                .sum();

        if (existing + quantity > bought) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        double price = order.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseThrow()
                .getUnitPrice();

        items.add(new ReturnedItem(product, quantity, price));
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public List<ReturnedItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getRefundTotal() {
        return items.stream()
                .mapToDouble(ReturnedItem::total)
                .sum();
    }

    public boolean isProcessed() {
        return processed;
    }

    public void markProcessed() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Return must contain an item");
        }

        processed = true;
    }
}