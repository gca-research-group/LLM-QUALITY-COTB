package com.store.payment;

import com.store.cashflow.CashflowService;
import com.store.order.Order;
import com.store.order.OrderService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentService {
    private final OrderService orderService;
    private final CashflowService cashflowService;
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private int paymentSequence = 0;

    public PaymentService(OrderService orderService, CashflowService cashflowService) {
        this.orderService = orderService;
        this.cashflowService = cashflowService;
    }

    public Payment registerPayment(String orderId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment value cannot be negative");
        }
        Order order = orderService.findById(orderId);
        order.markPaid();
        paymentSequence++;
        String paymentId = "PAY" + paymentSequence;
        Payment payment = new Payment(paymentId, orderId, amount);
        payments.put(paymentId, payment);
        cashflowService.recordInflow(amount, "Payment for order " + orderId);
        return payment;
    }

    public List<Payment> all() {
        return new ArrayList<>(payments.values());
    }
}