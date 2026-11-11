package store.model;

import java.util.*;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private double promotionDiscount;
    private double shippingCost;
    private OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (id == null || id.isBlank() || customer == null || items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public double getPromotionDiscount() { return promotionDiscount; }
    public double getShippingCost() { return shippingCost; }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public OrderStatus getStatus() { return status; }

    public void applyPromotion(double discount) {
        if (discount < 0) throw new IllegalArgumentException("Discount cannot be negative");
        promotionDiscount = Math.min(discount, getSubtotal());
    }

    public void applyShipping(double shipping) {
        if (shipping < 0) throw new IllegalArgumentException("Shipping cannot be negative");
        shippingCost = shipping;
    }

    public void markPaid() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public void markRefunded() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be refunded");
        }
        status = OrderStatus.REFUNDED;
    }
}