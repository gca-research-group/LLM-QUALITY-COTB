package store.model;

import store.exception.BusinessRuleException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private double promotionDiscount;
    private String appliedPromotionId;
    private double shippingCost;
    private String appliedShippingType;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one order item: " + id);
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
        this.appliedPromotionId = null;
        this.appliedShippingType = null;
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

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public String getAppliedPromotionId() {
        return appliedPromotionId;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public String getAppliedShippingType() {
        return appliedShippingType;
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (OrderItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public void setPromotionDiscount(String promotionId, double discount) {
        if (discount < 0) {
            throw new BusinessRuleException("Discount cannot be negative for order " + id);
        }
        this.appliedPromotionId = promotionId;
        this.promotionDiscount = discount;
    }

    public void setShipping(String shippingType, double cost) {
        if (cost < 0) {
            throw new BusinessRuleException("Shipping cost cannot be negative for order " + id);
        }
        this.appliedShippingType = shippingType;
        this.shippingCost = cost;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled order cannot be paid: " + id);
        }
        this.status = OrderStatus.PAID;
    }

    public void markCancelled() {
        if (status == OrderStatus.PAID) {
            throw new BusinessRuleException("Paid order cannot be cancelled: " + id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public int getOrderedQuantity(String productId) {
        int total = 0;
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                total += item.getQuantity();
            }
        }
        return total;
    }
}