package store.model;

import store.exception.InvalidOperationException;
import store.promotion.Promotion;
import store.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Promotion appliedPromotion;
    private double promotionDiscount;
    private ShippingRule appliedShipping;
    private double shippingCost;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOperationException("An order must contain at least one order item");
        }
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.PENDING;
        this.promotionDiscount = 0.0;
        this.shippingCost = 0.0;
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

    public double getShippingCost() {
        return shippingCost;
    }

    public Promotion getAppliedPromotion() {
        return appliedPromotion;
    }

    public ShippingRule getAppliedShipping() {
        return appliedShipping;
    }

    public double getSubtotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shippingCost;
    }

    public boolean applyPromotion(Promotion promotion) {
        if (promotion.isApplicable(this)) {
            this.appliedPromotion = promotion;
            this.promotionDiscount = promotion.calculateDiscount(this);
            return true;
        }
        return false;
    }

    public void applyShipping(ShippingRule shippingRule) {
        this.appliedShipping = shippingRule;
        this.shippingCost = shippingRule.calculateCost(this);
    }

    public void markPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOperationException("Order cannot be paid from status " + status + ": " + id);
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOperationException("Only pending orders can be cancelled (current status: " + status + "): " + id);
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void markRefunded() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOperationException("Only paid orders can be returned/refunded (current status: " + status + "): " + id);
        }
        this.status = OrderStatus.REFUNDED;
    }

    public OrderItem findItem(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }
}