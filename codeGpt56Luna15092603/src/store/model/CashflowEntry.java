package store.model;

import java.time.LocalDateTime;

public class CashflowEntry {
    public enum Type {
        INFLOW, OUTFLOW
    }

    private final String id;
    private final Type type;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;

    public CashflowEntry(String id, Type type, double amount, String description) {
        if (id == null || id.isBlank() || type == null || amount < 0) {
            throw new IllegalArgumentException("Invalid cashflow entry");
        }

        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description == null ? "" : description;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}