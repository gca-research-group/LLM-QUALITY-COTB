package store.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.model.Cart;
import store.model.CartItem;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Promotion;
import store.model.ShippingRule;

public class OrderService {
    private final Map<String, Order> orders = new LinkedHashMap<String, Order>();
    private final StockService stock;

    public OrderService(StockService stock) {
        this.stock = stock;
    }

    public Order createOrder(String id, Cart cart) {
        if (orders.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate order");
        }

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain items");
        }

        List<OrderItem> items = new ArrayList<OrderItem>();

        for (CartItem cartItem : cart.getItems()) {
            stock.decrease(cartItem.getProduct(), cartItem.getQuantity());
            items.add(new OrderItem(
                cartItem.getProduct(),
                cartItem.getQuantity(),
                cartItem.getProduct().getUnitPrice()
            ));
        }

        Order order = new Order(id, cart.getCustomer(), items);
        orders.put(id, order);
        return order;
    }

    public Order get(String id) {
        Order order = orders.get(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        return order;
    }

    public double applyPromotion(String id, Promotion promotion) {
        Order order = get(id);
        double discount = promotion.calculateDiscount(order);
        order.setPromotionDiscount(discount);
        return discount;
    }

    public void applyShipping(String id, ShippingRule rule) {
        get(id).setShippingCost(rule.getCost());
    }

    public void cancel(String id) {
        Order order = get(id);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        for (OrderItem item : order.getItems()) {
            stock.increase(item.getProduct(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    public java.util.Collection<Order> allOrders() {
        return orders.values();
    }
}