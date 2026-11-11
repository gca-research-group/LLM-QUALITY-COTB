package com.store.returns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final String orderId;
    private final List<ReturnItem> items = new ArrayList<>();
    private boolean processed;
    private double refundTotal;

    public ReturnRequest(String id, String orderId) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Return id cannot be null or empty");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("Order id cannot be null or empty");
        }
        this.id = id;
        this.orderId = orderId;
        this.processed = false;
        this.refundTotal = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void addItem(ReturnItem item) {
        if (processed) {
            throw new IllegalStateException("Cannot add items to a processed return request");
        }
        if (item == null) {
            throw new IllegalArgumentException("Return item cannot be null");
        }
        items.add(item);
    }

    public List<ReturnItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isProcessed() {
        return processed;
    }

    public void markProcessed(double refundTotal) {
        if (refundTotal < 0) {
            throw new IllegalArgumentException("Refund value cannot be negative");
        }
        this.processed = true;
        this.refundTotal = refundTotal;
    }

    public double getRefundTotal() {
        return refundTotal;
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }
}