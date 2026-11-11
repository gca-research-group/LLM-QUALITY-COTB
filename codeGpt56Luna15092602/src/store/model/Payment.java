package store.model;

public class Payment {
    private final String id;
    private final String orderId;
    private final double amount;

    public Payment(String id, String orderId, double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment cannot be negative");
        }

        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }
}