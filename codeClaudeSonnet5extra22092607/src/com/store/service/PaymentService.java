package com.store.service;

import com.store.exception.EntityNotFoundException;
import com.store.exception.InvalidAmountException;
import com.store.model.Order;
import com.store.model.Payment;
import com.store.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final OrderRepository orderRepository;
    private final CashflowService cashflowService;
    private final List<Payment> payments;

    public PaymentService(OrderRepository orderRepository, CashflowService cashflowService) {
        this.orderRepository = orderRepository;
        this.cashflowService = cashflowService;
        this.payments = new ArrayList<>();
    }

    public Payment registerPayment(String paymentId, String orderId, double amount) {
        if (amount < 0) {
            throw new InvalidAmountException("Payment amount cannot be negative");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        order.markPaid();
        Payment payment = new Payment(paymentId, orderId, amount);
        payments.add(payment);
        cashflowService.addInflow(amount, "Payment " + paymentId + " for order " + orderId);
        return payment;
    }

    public List<Payment> getAllPayments() {
        return payments;
    }
}