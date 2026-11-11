package store.service;

import store.exception.InvalidOperationException;
import store.model.Cart;
import store.model.CartItem;
import store.model.Order;
import store.model.OrderItem;
import store.promotion.Promotion;
import store.repository.OrderRepository;
import store.shipping.ShippingRule;

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
        if (cart.isEmpty()) {
            throw new InvalidOperationException("Cannot create an order from an empty cart: " + cart.getId());
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            orderItems.add(new OrderItem(cartItem.getProduct(), cartItem.getQuantity()));
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        for (OrderItem item : orderItems) {
            stockService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }
        orderRepository.add(order);
        return order;
    }

    public boolean applyPromotion(String orderId, Promotion promotion) {
        Order order = orderRepository.findById(orderId);
        return order.applyPromotion(promotion);
    }

    public void applyShipping(String orderId, ShippingRule shippingRule) {
        Order order = orderRepository.findById(orderId);
        order.applyShipping(shippingRule);
    }

    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        order.cancel();
        for (OrderItem item : order.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public Order findById(String orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> allOrders() {
        return new ArrayList<>(orderRepository.findAll());
    }
}