package store.model;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {
    private String id;
    private Order order;
    private List<ReturnItem> items;
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        if (id == null || order == null) {
            throw new IllegalArgumentException("Invalid return");
        }

        this.id = id;
        this.order = order;
        this.items = new ArrayList<ReturnItem>();
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public List<ReturnItem> getItems() {
        return new ArrayList<ReturnItem>(items);
    }

    public boolean isProcessed() {
        return processed;
    }

    public void addItem(ReturnItem item) {
        if (processed) {
            throw new IllegalStateException("Return already processed");
        }

        items.add(item);
    }

    public void markProcessed() {
        processed = true;
    }

    public double getRefundTotal() {
        if (items.size() == 0) {
            throw new IllegalArgumentException("Return must contain an item");
        }

        double total = 0;

        for (int i = 0; i < items.size(); i++) {
            ReturnItem item = items.get(i);
            int bought = order.getSoldQuantity(item.getProduct().getId());

            if (item.getQuantity() > bought) {
                throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
            }

            total += item.getQuantity() * findUnitPrice(item.getProduct().getId());
        }

        return total;
    }

    private double findUnitPrice(String productId) {
        List<OrderItem> orderItems = order.getItems();

        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getProduct().getId().equals(productId)) {
                return orderItems.get(i).getUnitPrice();
            }
        }

        return 0;
    }
}