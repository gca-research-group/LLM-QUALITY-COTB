package store.domain;
public record Payment(String id, String orderId, double amount, PaymentMethod method) {}
