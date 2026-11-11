package com.store.service;

import java.util.List;

import com.store.exception.InvalidOperationException;
import com.store.model.Order;
import com.store.model.Payment;
import com.store.repository.PaymentRepository;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CashflowService cashflowService;
    private int sequence = 0;

    public PaymentService(PaymentRepository paymentRepository, CashflowService cashflowService) {
        this.paymentRepository = paymentRepository;
        this.cashflowService = cashflowService;
    }

    public Payment registerPayment(Order order, double amount) {
        if (amount < 0) {
            throw new InvalidOperationException("Payment amount cannot be negative");
        }
        order.markPaid();
        sequence++;
        String paymentId = "PAY" + sequence;
        Payment payment = new Payment(paymentId, order, amount);
        paymentRepository.add(payment);
        cashflowService.registerInflow(amount, "Payment for order " + order.getId());
        return payment;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}