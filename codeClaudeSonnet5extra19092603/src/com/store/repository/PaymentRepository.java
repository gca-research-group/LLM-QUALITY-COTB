package com.store.repository;

import java.util.ArrayList;
import java.util.List;

import com.store.model.Payment;

public class PaymentRepository {
    private final List<Payment> payments = new ArrayList<>();

    public void add(Payment payment) {
        payments.add(payment);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments);
    }
}