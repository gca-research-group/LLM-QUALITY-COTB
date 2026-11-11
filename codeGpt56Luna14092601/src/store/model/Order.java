package store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    public enum Status { CREATED, PAID, CANCELLED, REFUNDED }

    public static class Item {
        private final Product product;
        private final int quantity;
        private final double unitPrice;

        public Item(Product product, int quantity, double unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double subtotal() { return quantity * unitPrice; }
    }

    private final String id;
    private final Customer customer;
    private final List<Item> items;
    private Status status = Status.CREATED;
    private double promotionDiscount;
    private double shippingCost;

    public Order(String id, Customer customer, List<Item> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order must contain an item");
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>(items);
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }
    public Status getStatus() { return status; }
    public double getPromotionDiscount() { return promotionDiscount; }
    public double getShippingCost() { return shippingCost; }

    public double subtotal() {
        double total = 0;
        for (Item item : items) total += item.subtotal();
        return total;
    }

    public double finalTotal() {
        return subtotal() - promotionDiscount + shippingCost;
    }

    public void setPromotionDiscount(double discount) {
        if (discount < 0) throw new IllegalArgumentException("Negative discount");
        promotionDiscount = Math.min(discount, subtotal());
    }

    public void setShippingCost(double cost) {
        if (cost < 0) throw new IllegalArgumentException("Negative shipping");
        shippingCost = cost;
    }

    public void markPaid() {
        if (status == Status.CANCELLED) throw new IllegalStateException("Cancelled orders cannot be paid");
        status = Status.PAID;
    }

    public void cancel() {
        if (status == Status.PAID) throw new IllegalStateException("Paid orders cannot be cancelled");
        if (status == Status.CANCELLED) throw new IllegalStateException("Order already cancelled");
        status = Status.CANCELLED;
    }

    public void markRefunded() {
        if (status != Status.PAID) throw new IllegalStateException("Only paid orders can be refunded");
        status = Status.REFUNDED;
    }
}