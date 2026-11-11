package store.model;

public class CashflowEntry {
    private final String id;
    private final CashflowType type;
    private final double amount;
    private final String description;

    public CashflowEntry(String id, CashflowType type, double amount, String description) {
        if (id == null || id.isBlank() || type == null || amount < 0 || description == null) {
            throw new IllegalArgumentException("Invalid cashflow entry");
        }

        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public String getId() { return id; }
    public CashflowType getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
}