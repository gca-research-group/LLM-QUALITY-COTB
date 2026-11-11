package com.store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.store.exception.InvalidOperationException;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private double shippingCost;
    private String appliedPromotionDescription;
    private String appliedShippingDescription;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOperationException("Each order must contain at least one order item");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
        this.appliedPromotionDescription = "None";
        this.appliedShippingDescription = "None";
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

    public double getSubtotal() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getSubtotal();
        }
        return sum;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double discount, String description) {
        if (discount < 0) {
            throw new InvalidOperationException("Promotion discount cannot be negative");
        }
        this.promotionDiscount = discount;
        this.appliedPromotionDescription = description;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double cost, String description) {
        if (cost < 0) {
            throw new InvalidOperationException("Shipping cost cannot be negative");
        }
        this.shippingCost = cost;
        this.appliedShippingDescription = description;
    }

    public String getAppliedPromotionDescription() {
        return appliedPromotionDescription;
    }

    public String getAppliedShippingDescription() {
        return appliedShippingDescription;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOperationException("Cancelled orders cannot be paid");
        }
        if (status == OrderStatus.PAID) {
            throw new InvalidOperationException("Order " + id + " is already paid");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new InvalidOperationException("Paid orders cannot be cancelled");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOperationException("Order " + id + " is already cancelled");
        }
        status = OrderStatus.CANCELLED;
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

    public double getUnitPriceForProduct(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getUnitPrice();
            }
        }
        throw new InvalidOperationException("Product " + productId + " is not part of order " + id);
    }
}