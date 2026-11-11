package store.service;

import store.exception.BusinessRuleException;
import store.model.Order;
import store.model.OrderStatus;
import store.model.Payment;
import store.repository.PaymentRepository;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final CashflowService cashflowService;
    private int paymentCounter;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, CashflowService cashflowService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.cashflowService = cashflowService;
        this.paymentCounter = 0;
    }

    public Payment registerPayment(String orderId, double amount, String method) {
        if (amount < 0) {
            throw new BusinessRuleException("Payment amount cannot be negative for order " + orderId);
        }
        Order order = orderService.findOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot pay a cancelled order: " + orderId);
        }
        orderService.markPaid(orderId);
        paymentCounter++;
        String paymentId = "PAY" + paymentCounter;
        Payment payment = new Payment(paymentId, order, amount, method);
        paymentRepository.add(payment);
        cashflowService.recordInflow(amount, "Payment for order " + orderId);
        return payment;
    }
}