package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.order.Order;
import com.store.order.OrderStatus;
import com.store.payment.Payment;

public class PaymentService {
    private final OrderService orderService;
    private final CashflowService cashflowService;

    public PaymentService(OrderService orderService, CashflowService cashflowService) {
        this.orderService = orderService;
        this.cashflowService = cashflowService;
    }

    public Payment registerPayment(String paymentId, String orderId, double amount) {
        Order order = orderService.getOrderOrThrow(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new StoreOperationException("Cannot pay a cancelled order: " + orderId);
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new StoreOperationException("Order already paid: " + orderId);
        }
        Payment payment = new Payment(paymentId, orderId, amount);
        order.markPaid();
        cashflowService.recordInflow("Payment for order " + orderId, amount);
        return payment;
    }
}