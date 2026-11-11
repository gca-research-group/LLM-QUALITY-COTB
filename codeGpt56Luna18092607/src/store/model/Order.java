package store.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private Customer customer;
    private List<OrderItem> items;
    private double promotionDiscount;
    private double shipping;
    private OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items) {
        if (id == null || id.length() == 0 || customer == null || items == null || items.size() == 0) {
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
        return new ArrayList<OrderItem>(items);
    }

    public double getSubtotal() {
        double total = 0;

        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getSubtotal();
        }

        return total;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public double getShipping() {
        return shipping;
    }

    public double getFinalTotal() {
        return getSubtotal() - promotionDiscount + shipping;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setPromotionDiscount(double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Negative discount");
        }

        promotionDiscount = discount;
    }

    public void setShipping(double shipping) {
        if (shipping < 0) {
            throw new IllegalArgumentException("Negative shipping");
        }

        this.shipping = shipping;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getSoldQuantity(String productId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProduct().getId().equals(productId)) {
                return items.get(i).getQuantity();
            }
        }

        return 0;
    }
}