package store.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReturnRequest {
    private final String id;
    private final Order order;
    private final Map<String, Integer> quantities = new LinkedHashMap<String, Integer>();
    private boolean processed;

    public ReturnRequest(String id, Order order) {
        if (id == null || order == null || order.getStatus() != OrderStatus.PAID) {
            throw new IllegalArgumentException("Only paid orders can be returned");
        }

        this.id = id;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Map<String, Integer> getQuantities() {
        return quantities;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void addItem(String productId, int quantity) {
        if (processed || quantity <= 0) {
            throw new IllegalArgumentException("Invalid return item");
        }

        OrderItem target = null;

        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                target = item;
            }
        }

        if (target == null) {
            throw new IllegalArgumentException("Product was not bought");
        }

        int current = quantities.containsKey(productId)
                ? quantities.get(productId).intValue()
                : 0;

        if (current + quantity > target.getQuantity()) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        quantities.put(productId, Integer.valueOf(current + quantity));
    }

    public double calculateRefundTotal() {
        double total = 0;

        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getId().equals(entry.getKey())) {
                    total += item.getUnitPrice() * entry.getValue().intValue();
                }
            }
        }

        return total;
    }

    public void markProcessed() {
        if (quantities.size() == 0) {
            throw new IllegalArgumentException("Return must contain an item");
        }

        processed = true;
    }
}