package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final List<ReturnItem> items = new ArrayList<ReturnItem>();
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        if (id == null || order == null) {
            throw new IllegalArgumentException("Invalid return");
        }

        this.id = id;
        this.order = order;
    }

    public void addItem(Product product, int quantity) {
        if (processed) {
            throw new IllegalStateException("Return already processed");
        }

        int bought = 0;
        int alreadyReturned = 0;

        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                bought += item.getQuantity();
            }
        }

        for (ReturnItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                alreadyReturned += item.getQuantity();
            }
        }

        if (quantity <= 0 || alreadyReturned + quantity > bought) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        items.add(new ReturnItem(product, quantity, findPrice(product)));
    }

    private double findPrice(Product product) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                return item.getUnitPrice();
            }
        }

        return 0;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public List<ReturnItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getRefundTotal() {
        double total = 0;

        for (ReturnItem item : items) {
            total += item.getRefund();
        }

        return total;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void markProcessed() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Return must contain items");
        }

        processed = true;
    }
}