package com.store.model;

import java.util.ArrayList;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final List<ReturnItem> items = new ArrayList<>();
    private boolean processed;
    private double refundTotal;

    public ReturnRequest(String id, Order order) {
        this.id = id;
        this.order = order;
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
        return new ArrayList<>(items);
    }

    public boolean isProcessed() {
        return processed;
    }

    public double getRefundTotal() {
        return refundTotal;
    }

    public void addItem(ReturnItem item) {
        if (processed) {
            throw new IllegalStateException("Cannot modify a return request that was already processed");
        }
        items.add(item);
    }

    public void markProcessed(double refundTotal) {
        if (processed) {
            throw new IllegalStateException("Return request was already processed");
        }
        this.processed = true;
        this.refundTotal = refundTotal;
    }
}