package com.store.service;

import com.store.cart.Cart;
import com.store.cart.CartItem;
import com.store.exception.StoreOperationException;
import com.store.order.Order;
import com.store.order.OrderItem;
import com.store.order.OrderStatus;
import com.store.promotion.Promotion;
import com.store.repository.OrderRepository;
import com.store.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final StockService stockService;

    public OrderService(OrderRepository orderRepository, CartService cartService, StockService stockService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.stockService = stockService;
    }

    public Order createOrderFromCart(String orderId, String cartId) {
        Cart cart = cartService.getCartOrThrow(cartId);
        if (cart.isEmpty()) {
            throw new StoreOperationException("Cannot create an order from an empty cart: " + cartId);
        }
        List<CartItem> cartItems = cart.getItems();
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        for (CartItem cartItem : cartItems) {
            double unitPrice = cartItem.getProduct().getUnitPrice();
            orderItems.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity(), unitPrice));
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        for (OrderItem item : orderItems) {
            stockService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }
        orderRepository.add(order);
        return order;
    }

    public Order getOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new StoreOperationException("Order not found: " + orderId));
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = getOrderOrThrow(orderId);
        double discount = 0.0;
        if (promotion.isApplicable(order, order.getCustomer())) {
            discount = promotion.calculateDiscount(order, order.getCustomer());
        }
        order.setPromotion(promotion.getName(), discount);
    }

    public void applyBestPromotion(String orderId, List<Promotion> promotions) {
        Order order = getOrderOrThrow(orderId);
        String bestName = "NONE";
        double bestDiscount = 0.0;
        for (Promotion promotion : promotions) {
            if (promotion.isApplicable(order, order.getCustomer())) {
                double discount = promotion.calculateDiscount(order, order.getCustomer());
                if (discount > bestDiscount) {
                    bestDiscount = discount;
                    bestName = promotion.getName();
                }
            }
        }
        order.setPromotion(bestName, bestDiscount);
    }

    public void applyShipping(String orderId, ShippingRule shippingRule) {
        Order order = getOrderOrThrow(orderId);
        double cost = shippingRule.calculateCost(order);
        order.setShipping(shippingRule.getName(), cost);
    }

    public void cancelOrder(String orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getStatus() == OrderStatus.PAID) {
            throw new StoreOperationException("Paid orders cannot be cancelled: " + orderId);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new StoreOperationException("Order already cancelled: " + orderId);
        }
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        order.markCancelled();
    }

    public List<Order> listAll() {
        return orderRepository.findAll();
    }
}