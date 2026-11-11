package com.store.order;

import com.store.cart.Cart;
import com.store.cart.CartItem;
import com.store.catalog.StockService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final StockService stockService;
    private final Map<String, Order> orders = new LinkedHashMap<>();

    public OrderService(StockService stockService) {
        this.stockService = stockService;
    }

    public Order createOrderFromCart(String orderId, Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot create an order from an empty cart");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            stockService.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
            orderItems.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getUnitPrice()));
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        orders.put(orderId, order);
        return order;
    }

    public Order findById(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public void cancelOrder(String orderId) {
        Order order = findById(orderId);
        order.cancel();
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public List<Order> all() {
        return new ArrayList<>(orders.values());
    }
}