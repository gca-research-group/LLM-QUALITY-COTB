package store.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final Map<String, Integer> items = new LinkedHashMap<>();
    private double refundTotal;
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        this.id = id;
        this.order = order;
    }

    public String getId() { return id; }
    public Order getOrder() { return order; }
    public Map<String, Integer> getItems() { return new LinkedHashMap<>(items); }
    public double getRefundTotal() { return refundTotal; }
    public boolean isProcessed() { return processed; }

    public void add(String productId, int quantity) {
        if (processed) throw new IllegalStateException("Return already processed");
        if (quantity <= 0) throw new IllegalArgumentException("Returned quantity must be positive");
        items.merge(productId, quantity, Integer::sum);
    }

    public void setRefundTotal(double refundTotal) {
        this.refundTotal = refundTotal;
    }

    public void markProcessed() {
        processed = true;
    }
}