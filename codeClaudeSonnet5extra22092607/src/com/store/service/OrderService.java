package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EmptyOrderException;
import com.store.exception.EntityNotFoundException;
import com.store.model.Cart;
import com.store.model.CartItem;
import com.store.model.Customer;
import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.promotion.Promotion;
import com.store.repository.OrderRepository;
import com.store.shipping.ShippingRule;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final StockService stockService;

    public OrderService(OrderRepository orderRepository, StockService stockService) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
    }

    public Order createOrderFromCart(String orderId, Cart cart) {
        if (orderRepository.existsById(orderId)) {
            throw new DuplicateIdException("Duplicate order id: " + orderId);
        }
        if (cart.getItems().isEmpty()) {
            throw new EmptyOrderException("Cart is empty, cannot create order: " + cart.getId());
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            stockService.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
            orderItems.add(new OrderItem(
                    cartItem.getProduct().getId(),
                    cartItem.getProduct().getName(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getUnitPrice()
            ));
        }
        Order order = new Order(orderId, cart.getCustomerId(), orderItems);
        orderRepository.save(order);
        cart.clear();
        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion, Customer customer) {
        Order order = findOrder(orderId);
        if (promotion != null && promotion.isApplicable(order, customer)) {
            double discount = Math.min(promotion.calculateDiscount(order, customer), order.getSubtotal());
            order.setPromotion(discount, promotion.getName());
        } else {
            order.setPromotion(0.0, null);
        }
    }

    public void applyShipping(String orderId, ShippingRule rule) {
        Order order = findOrder(orderId);
        double cost = rule.calculateCost(order);
        order.setShipping(cost, rule.getName());
    }

    public void cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        order.cancel();
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProductId(), item.getQuantity());
        }
    }

    public Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
    }
}