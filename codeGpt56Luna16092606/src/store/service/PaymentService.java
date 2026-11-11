package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.CashflowEntry;
import store.model.CashflowType;
import store.model.Order;
import store.model.OrderStatus;
import store.model.Payment;

public class PaymentService {
    private final List<Payment> payments = new ArrayList<Payment>();
    private final CashflowService cashflow;

    public PaymentService(CashflowService cashflow) {
        this.cashflow = cashflow;
    }

    public Payment pay(String id, Order order, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Negative payment");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }

        if (amount < order.getFinalTotal()) {
            throw new IllegalArgumentException("Insufficient payment");
        }

        Payment payment = new Payment(
            id,
            order.getId(),
            amount,
            true
        );

        payments.add(payment);
        order.setStatus(OrderStatus.PAID);

        cashflow.record(new CashflowEntry(
            "PAY-" + id,
            CashflowType.INFLOW,
            amount,
            "Payment for " + order.getId()
        ));

        return payment;
    }
}