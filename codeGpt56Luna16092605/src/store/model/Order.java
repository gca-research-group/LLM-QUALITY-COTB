package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private double promotionDiscount;
    private double shippingCost;
    private OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (id == null || customer == null || items == null || items.size() == 0) {
            throw new IllegalArgumentException("Order must contain items");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<OrderItem>(items);
        this.status = OrderStatus.NEW;
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

    public double getSubtotal() {
        double total = 0;

        for (OrderItem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setPromotionDiscount(double value) {
        if (value < 0 || value > getSubtotal()) {
            throw new IllegalArgumentException("Invalid discount");
        }
        promotionDiscount = value;
    }

    public void setShippingCost(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid shipping");
        }
        shippingCost = value;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled order cannot be paid");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalArgumentException("Paid order cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }
}