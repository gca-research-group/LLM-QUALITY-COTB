package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturnRequest {
    public static class ReturnedItem {
        private final String productId;
        private final int quantity;
        private final double unitPrice;

        public ReturnedItem(String productId, int quantity, double unitPrice) {
            if (productId == null || productId.isBlank() || quantity <= 0 || unitPrice < 0) {
                throw new IllegalArgumentException("Invalid returned item");
            }

            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double subtotal() {
            return quantity * unitPrice;
        }
    }

    private final String id;
    private final String orderId;
    private final List<ReturnedItem> items = new ArrayList<>();
    private boolean processed;

    public ReturnRequest(String id, String orderId) {
        if (id == null || id.isBlank() || orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Invalid return request");
        }

        this.id = id;
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public List<ReturnedItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void add(ReturnedItem item) {
        if (processed) {
            throw new IllegalStateException("Return already processed");
        }

        items.add(item);
    }

    public double refundTotal() {
        return items.stream()
                .mapToDouble(ReturnedItem::subtotal)
                .sum();
    }

    public boolean isProcessed() {
        return processed;
    }

    public void markProcessed() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Return must contain at least one item");
        }

        processed = true;
    }
}