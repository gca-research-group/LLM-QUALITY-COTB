package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    public enum Status {
        CREATED, PAID, CANCELLED, REFUNDED
    }

    public static class OrderItem {
        private final String productId;
        private final String productName;
        private final int quantity;
        private final double unitPrice;

        public OrderItem(String productId, String productName, int quantity, double unitPrice) {
            if (quantity <= 0 || unitPrice < 0) {
                throw new IllegalArgumentException("Invalid order item");
            }
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
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
    private final Customer customer;
    private final List<OrderItem> items;
    private double promotionDiscount;
    private double shippingCost;
    private Status status;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (id == null || id.isBlank() || customer == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = Status.CREATED;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Status getStatus() {
        return status;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public double subtotal() {
        return items.stream()
                .mapToDouble(OrderItem::subtotal)
                .sum();
    }

    public void setPromotionDiscount(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }

        promotionDiscount = Math.min(value, subtotal());
    }

    public void setShippingCost(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Shipping cannot be negative");
        }

        shippingCost = value;
    }

    public double finalTotal() {
        return subtotal() - promotionDiscount + shippingCost;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}