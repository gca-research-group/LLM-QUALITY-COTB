package com.store.order;

import com.store.exception.StoreOperationException;
import com.store.model.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private String appliedPromotionName;
    private double shippingCost;
    private String appliedShippingName;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new StoreOperationException("Order must contain at least one order item");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<OrderItem>(items);
        this.status = OrderStatus.CREATED;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
        this.appliedPromotionName = "NONE";
        this.appliedShippingName = "NONE";
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

    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    public String getAppliedShippingName() {
        return appliedShippingName;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public void setPromotion(String name, double discount) {
        this.appliedPromotionName = name;
        this.promotionDiscount = discount;
    }

    public void setShipping(String name, double cost) {
        this.appliedShippingName = name;
        this.shippingCost = cost;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new StoreOperationException("Cancelled orders cannot be paid: " + id);
        }
        this.status = OrderStatus.PAID;
    }

    public void markCancelled() {
        if (status == OrderStatus.PAID) {
            throw new StoreOperationException("Paid orders cannot be cancelled: " + id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public int getPurchasedQuantity(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public double getUnitPriceForProduct(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getUnitPrice();
            }
        }
        return 0.0;
    }
}