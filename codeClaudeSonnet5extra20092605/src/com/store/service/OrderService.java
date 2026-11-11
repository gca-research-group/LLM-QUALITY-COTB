package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Cart;
import com.store.model.CartItem;
import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.model.OrderStatus;
import com.store.promotion.Promotion;
import com.store.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Integer> soldQuantities = new LinkedHashMap<>();
    private final StockService stockService;

    public OrderService(StockService stockService) {
        this.stockService = stockService;
    }

    public Order createOrderFromCart(String orderId, Cart cart) {
        if (orders.containsKey(orderId)) {
            throw new BusinessRuleException("Duplicate order identifier: " + orderId);
        }
        if (cart.isEmpty()) {
            throw new BusinessRuleException("Cannot create an order from an empty cart");
        }
        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            items.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getUnitPrice()));
        }
        Order order = new Order(orderId, cart.getCustomer(), items);
        for (OrderItem item : items) {
            stockService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }
        orders.put(orderId, order);
        return order;
    }

    public Order findById(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new BusinessRuleException("Order not found: " + orderId);
        }
        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = findById(orderId);
        double discount = promotion.isApplicable(order) ? promotion.calculateDiscount(order) : 0.0;
        order.applyPromotion(discount, promotion.getDescription());
    }

    public void applyShipping(String orderId, ShippingRule shippingRule) {
        Order order = findById(orderId);
        double cost = shippingRule.calculateCost(order);
        order.applyShipping(cost, shippingRule.getName());
    }

    public void markPaid(String orderId) {
        Order order = findById(orderId);
        order.markPaid();
        for (OrderItem item : order.getItems()) {
            soldQuantities.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
        }
    }

    public void cancelOrder(String orderId) {
        Order order = findById(orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessRuleException("Paid orders cannot be cancelled: " + orderId);
        }
        order.cancel();
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public List<Order> allOrders() {
        return new ArrayList<>(orders.values());
    }

    public Map<String, Integer> getSoldQuantities() {
        return new LinkedHashMap<>(soldQuantities);
    }
}