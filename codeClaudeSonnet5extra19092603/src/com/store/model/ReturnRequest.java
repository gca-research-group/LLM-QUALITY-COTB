package com.store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.store.exception.InvalidOperationException;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final List<ReturnItem> items;
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        this.id = id;
        this.order = order;
        this.items = new ArrayList<>();
        this.processed = false;
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

    public void addItem(ReturnItem item) {
        if (processed) {
            throw new InvalidOperationException("Cannot add items to an already processed return request " + id);
        }
        items.add(item);
    }

    public double calculateRefundTotal() {
        double sum = 0.0;
        for (ReturnItem item : items) {
            sum += item.getRefundAmount();
        }
        return sum;
    }

    public void markProcessed() {
        if (items.isEmpty()) {
            throw new InvalidOperationException("A return must contain at least one returned item");
        }
        if (processed) {
            throw new InvalidOperationException("Return request " + id + " was already processed");
        }
        this.processed = true;
    }
}