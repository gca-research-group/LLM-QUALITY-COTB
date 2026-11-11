package com.store.returns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final String orderId;
    private final List<ReturnItem> items = new ArrayList<ReturnItem>();
    private boolean processed;

    public ReturnRequest(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
        this.processed = false;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<ReturnItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isProcessed() {
        return processed;
    }

    public void addItem(ReturnItem item) {
        items.add(item);
    }

    public double getRefundTotal() {
        double total = 0;
        for (ReturnItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void markProcessed() {
        this.processed = true;
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }
}