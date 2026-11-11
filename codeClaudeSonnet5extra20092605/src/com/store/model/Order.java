package com.store.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private double shippingCost;
    private String promotionDescription;
    private String shippingDescription;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one order item");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
        this.promotionDescription = "NONE";
        this.shippingDescription = "NONE";
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public String getShippingDescription() {
        return shippingDescription;
    }

    public double getSubtotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public void applyPromotion(double discount, String description) {
        if (discount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }
        this.promotionDiscount = discount;
        this.promotionDescription = description;
    }

    public void applyShipping(double cost, String description) {
        if (cost < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        this.shippingCost = cost;
        this.shippingDescription = description;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public int getPurchasedQuantity(String productId) {
        int total = 0;
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                total += item.getQuantity();
            }
        }
        return total;
    }
}