package store.service;

import store.exception.InvalidAmountException;
import store.model.Order;
import store.model.Payment;
import store.repository.PaymentRepository;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CashflowService cashflowService;
    private int counter;

    public PaymentService(PaymentRepository paymentRepository, CashflowService cashflowService) {
        this.paymentRepository = paymentRepository;
        this.cashflowService = cashflowService;
        this.counter = 0;
    }

    public Payment registerPayment(Order order, double amount) {
        if (amount < 0) {
            throw new InvalidAmountException("Payment amount cannot be negative");
        }
        order.markPaid();
        counter++;
        String paymentId = "PAY" + counter;
        Payment payment = new Payment(paymentId, order, amount);
        paymentRepository.add(payment);
        cashflowService.recordInflow(amount, "Payment for order " + order.getId());
        return payment;
    }
}