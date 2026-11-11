package store.model;

import store.exception.InvalidOperationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final List<ReturnItem> items;
    private boolean processed;
    private double refundTotal;

    public ReturnRequest(String id, Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException("Only paid orders can be returned: " + order.getId());
        }
        this.id = id;
        this.order = order;
        this.items = new ArrayList<>();
        this.processed = false;
        this.refundTotal = 0.0;
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

    public boolean isProcessed() {
        return processed;
    }

    public double getRefundTotal() {
        return refundTotal;
    }

    public void addItem(ReturnItem item) {
        if (processed) {
            throw new InvalidOperationException("Cannot add items to an already processed return: " + id);
        }
        OrderItem orderItem = order.findItem(item.getProduct().getId());
        if (orderItem == null) {
            throw new InvalidOperationException(
                    "Product " + item.getProduct().getId() + " was not part of order " + order.getId());
        }
        if (item.getQuantity() > orderItem.getRemainingReturnableQuantity()) {
            throw new InvalidOperationException(
                    "Returned quantity exceeds quantity originally bought for product " + item.getProduct().getId());
        }
        items.add(item);
    }

    public void markProcessed(double refundTotal) {
        this.processed = true;
        this.refundTotal = refundTotal;
    }
}