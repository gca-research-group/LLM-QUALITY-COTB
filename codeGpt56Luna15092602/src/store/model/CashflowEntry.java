package store.model;

public class CashflowEntry {
    public enum Type {
        INFLOW, OUTFLOW
    }

    private final String id;
    private final Type type;
    private final double amount;
    private final String description;

    public CashflowEntry(String id, Type type, double amount, String description) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cashflow value cannot be negative");
        }

        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
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
}