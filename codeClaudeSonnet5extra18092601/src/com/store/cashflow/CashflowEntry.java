package com.store.cashflow;

public class CashflowEntry {
    private final String id;
    private final CashflowType type;
    private final double amount;
    private final String description;

    public CashflowEntry(String id, CashflowType type, double amount, String description) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Cashflow entry id cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Cashflow type cannot be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Cashflow amount cannot be negative");
        }
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description == null ? "" : description;
    }

    public String getId() {
        return id;
    }

    public CashflowType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}