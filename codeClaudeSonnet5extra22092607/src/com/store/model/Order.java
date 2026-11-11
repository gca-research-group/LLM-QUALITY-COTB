package com.store.model;

import com.store.exception.EmptyOrderException;
import com.store.exception.InvalidOrderStateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private String promotionName;
    private double shippingCost;
    private String shippingName;

    public Order(String id, String customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyOrderException("An order must contain at least one order item");
        }
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.promotionDiscount = 0.0;
        this.promotionName = null;
        this.shippingCost = 0.0;
        this.shippingName = null;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public double getSubtotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void setPromotion(double discount, String name) {
        this.promotionDiscount = discount;
        this.promotionName = name;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setShipping(double cost, String name) {
        this.shippingCost = cost;
        this.shippingName = name;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public String getShippingName() {
        return shippingName;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public OrderItem findItemByProductId(String productId) {
        for (OrderItem item : items) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cancelled orders cannot be paid: " + id);
        }
        if (status == OrderStatus.PAID) {
            throw new InvalidOrderStateException("Order is already paid: " + id);
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new InvalidOrderStateException("Paid orders cannot be cancelled: " + id);
        }
        if (status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled: " + id);
        }
        status = OrderStatus.CANCELLED;
    }
}