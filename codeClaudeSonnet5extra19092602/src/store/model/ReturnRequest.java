package store.model;

import store.exception.BusinessRuleException;

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
            throw new BusinessRuleException("Only paid orders can be returned: " + order.getId());
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
            throw new BusinessRuleException("Cannot add items to an already processed return: " + id);
        }
        int alreadyReturned = 0;
        for (ReturnItem existing : items) {
            if (existing.getProduct().getId().equals(item.getProduct().getId())) {
                alreadyReturned += existing.getQuantity();
            }
        }
        int originallyBought = order.getOrderedQuantity(item.getProduct().getId());
        if (alreadyReturned + item.getQuantity() > originallyBought) {
            throw new BusinessRuleException("Returned quantity exceeds originally bought quantity for product " + item.getProduct().getId());
        }
        items.add(item);
    }

    public double calculateRefundTotal() {
        double total = 0.0;
        for (ReturnItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void markProcessed(double refundTotal) {
        if (processed) {
            throw new BusinessRuleException("Return already processed: " + id);
        }
        if (items.isEmpty()) {
            throw new BusinessRuleException("Return must contain at least one returned item: " + id);
        }
        this.refundTotal = refundTotal;
        this.processed = true;
    }
}