package store.service;

import store.exception.BusinessRuleException;
import store.model.Cart;
import store.model.CartItem;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.promotion.Promotion;
import store.repository.OrderRepository;
import store.shipping.ShippingRule;
import store.shipping.ShippingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogService catalogService;
    private final PromotionService promotionService;
    private final ShippingService shippingService;

    public OrderService(OrderRepository orderRepository, CatalogService catalogService,
                         PromotionService promotionService, ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.catalogService = catalogService;
        this.promotionService = promotionService;
        this.shippingService = shippingService;
    }

    public Order createOrderFromCart(String orderId, Cart cart) {
        if (cart.isEmpty()) {
            throw new BusinessRuleException("Cannot create an order from an empty cart: " + cart.getId());
        }
        List<OrderItem> orderItems = new ArrayList<>();
        List<String> reservedProductIds = new ArrayList<>();
        List<Integer> reservedQuantities = new ArrayList<>();
        try {
            for (CartItem cartItem : cart.getItems()) {
                String productId = cartItem.getProduct().getId();
                int quantity = cartItem.getQuantity();
                catalogService.decreaseStock(productId, quantity);
                reservedProductIds.add(productId);
                reservedQuantities.add(quantity);
                orderItems.add(new OrderItem(cartItem.getProduct(), quantity, cartItem.getProduct().getUnitPrice()));
            }
        } catch (BusinessRuleException ex) {
            for (int i = 0; i < reservedProductIds.size(); i++) {
                catalogService.increaseStock(reservedProductIds.get(i), reservedQuantities.get(i));
            }
            throw ex;
        }
        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        orderRepository.add(order);
        return order;
    }

    public Order findOrder(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new BusinessRuleException("Order not found: " + orderId);
        }
        return order;
    }

    public double applyPromotion(String orderId, String promotionId) {
        Order order = findOrder(orderId);
        Promotion promotion = promotionService.findPromotion(promotionId);
        if (!promotion.isApplicable(order, order.getCustomer())) {
            throw new BusinessRuleException("Promotion " + promotionId + " is not applicable to order " + orderId);
        }
        double discount = promotion.calculateDiscount(order, order.getCustomer());
        order.setPromotionDiscount(promotionId, discount);
        return discount;
    }

    public double applyShipping(String orderId, ShippingType shippingType) {
        Order order = findOrder(orderId);
        ShippingRule rule = shippingService.findRule(shippingType);
        double cost = rule.calculateCost(order);
        order.setShipping(shippingType.name(), cost);
        return cost;
    }

    public double calculateFinalTotal(String orderId) {
        return findOrder(orderId).getFinalTotal();
    }

    public void markPaid(String orderId) {
        findOrder(orderId).markPaid();
    }

    public void cancelOrder(String orderId) {
        Order order = findOrder(orderId);
        order.markCancelled();
        for (OrderItem item : order.getItems()) {
            catalogService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Map<String, Integer> salesByProduct() {
        Map<String, Integer> sales = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            for (OrderItem item : order.getItems()) {
                sales.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
            }
        }
        return sales;
    }

    public Map<OrderStatus, Integer> ordersByStatus() {
        Map<OrderStatus, Integer> counts = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderRepository.findAll()) {
            counts.merge(order.getStatus(), 1, Integer::sum);
        }
        return counts;
    }
}