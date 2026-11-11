package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Order;
import com.store.model.OrderStatus;
import com.store.model.Payment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentService {
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private final OrderService orderService;
    private final CashflowService cashflowService;

    public PaymentService(OrderService orderService, CashflowService cashflowService) {
        this.orderService = orderService;
        this.cashflowService = cashflowService;
    }

    public Payment registerPayment(String paymentId, String orderId, double amount) {
        if (payments.containsKey(paymentId)) {
            throw new BusinessRuleException("Duplicate payment identifier: " + paymentId);
        }
        Order order = orderService.findById(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot pay a cancelled order: " + orderId);
        }
        if (amount < 0) {
            throw new BusinessRuleException("Payment amount cannot be negative");
        }
        Payment payment = new Payment(paymentId, order, amount);
        payments.put(paymentId, payment);
        orderService.markPaid(orderId);
        cashflowService.recordInflow("CF-" + paymentId, amount, "Payment for order " + orderId);
        return payment;
    }

    public List<Payment> allPayments() {
        return new ArrayList<>(payments.values());
    }
}