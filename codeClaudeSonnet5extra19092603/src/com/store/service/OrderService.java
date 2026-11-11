package com.store.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.store.exception.InvalidOperationException;
import com.store.exception.NotFoundException;
import com.store.model.Cart;
import com.store.model.CartItem;
import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.repository.OrderRepository;

public class OrderService {
    private final OrderRepository orderRepository;
    private final StockService stockService;

    public OrderService(OrderRepository orderRepository, StockService stockService) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
    }

    public Order createOrderFromCart(String orderId, Cart cart) {
        if (cart.isEmpty()) {
            throw new InvalidOperationException("Cannot create an order from an empty cart");
        }
        List<CartItem> cartItems = cart.getItems();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getUnitPrice()));
        }
        for (CartItem cartItem : cartItems) {
            stockService.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        orderRepository.add(order);
        for (CartItem cartItem : cartItems) {
            cart.removeProduct(cartItem.getProduct());
        }
        return order;
    }

    public Order requireOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    public void cancelOrder(String orderId) {
        Order order = requireOrder(orderId);
        order.cancel();
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public Collection<Order> findAll() {
        return orderRepository.findAll();
    }
}