package store.repository;

import store.model.Payment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentRepository {
    private final Map<String, Payment> payments = new LinkedHashMap<>();

    public void add(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }
}