package store.domain;
public record CashflowEntry(String id, CashflowType type, double amount, String description) {}
