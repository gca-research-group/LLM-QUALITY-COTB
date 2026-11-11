package store.model;

public class Payment {
    private String id;
    private String orderId;
    private double amount;

    public Payment(String id, String orderId, double amount) {
        if (id == null || orderId == null || amount < 0) {
            throw new IllegalArgumentException("Invalid payment");
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