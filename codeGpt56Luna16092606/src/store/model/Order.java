package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private double shippingCost;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (id == null || id.length() == 0 || customer == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain items");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<OrderItem>(items);
        this.status = OrderStatus.CREATED;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public void setPromotionDiscount(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative discount");
        }

        promotionDiscount = value;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative shipping");
        }

        shippingCost = value;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }
}