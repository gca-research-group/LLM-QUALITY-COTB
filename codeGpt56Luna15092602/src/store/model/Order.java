package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    public enum Status {
        CREATED, PAID, CANCELLED, REFUNDED
    }

    public static class OrderItem {
        private final Product product;
        private final int quantity;
        private final double unitPrice;

        public OrderItem(Product product, int quantity, double unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Product getProduct() {
            return product;
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
    private Status status = Status.CREATED;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
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

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::subtotal)
                .sum();
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }

        promotionDiscount = Math.min(value, getSubtotal());
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Shipping cannot be negative");
        }

        shippingCost = value;
    }

    public double getFinalTotal() {
        return Math.max(0, getSubtotal() - promotionDiscount + shippingCost);
    }

    public void markPaid() {
        if (status == Status.CANCELLED) {
            throw new IllegalStateException("Cancelled order cannot be paid");
        }

        if (status != Status.CREATED) {
            throw new IllegalStateException("Order cannot be paid");
        }

        status = Status.PAID;
    }

    public void cancel() {
        if (status == Status.PAID) {
            throw new IllegalStateException("Paid order cannot be cancelled");
        }

        if (status != Status.CREATED) {
            throw new IllegalStateException("Order cannot be cancelled");
        }

        status = Status.CANCELLED;
    }

    public void markRefunded() {
        if (status != Status.PAID) {
            throw new IllegalStateException("Only paid order can be refunded");
        }

        status = Status.REFUNDED;
    }
}