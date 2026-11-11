package com.store.model;

import com.store.exception.EmptyReturnException;
import com.store.exception.InvalidOrderStateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    private final String id;
    private final String orderId;
    private final List<ReturnItem> items;
    private boolean processed;
    private double refundTotal;

    public ReturnRequest(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
        this.items = new ArrayList<>();
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
            throw new InvalidOrderStateException("Return request already processed: " + id);
        }
        items.add(item);
    }

    public List<ReturnItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void markProcessed(double total) {
        if (processed) {
            throw new InvalidOrderStateException("Return request already processed: " + id);
        }
        if (items.isEmpty()) {
            throw new EmptyReturnException("Return request must contain at least one item: " + id);
        }
        processed = true;
        refundTotal = total;
    }

    public boolean isProcessed() {
        return processed;
    }

    public double getRefundTotal() {
        return refundTotal;
    }
}