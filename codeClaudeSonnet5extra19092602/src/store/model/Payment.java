package store.model;

public class Payment {
    private final String id;
    private final Order order;
    private final double amount;
    private final String method;

    public Payment(String id, Order order, double amount, String method) {
        this.id = id;
        this.order = order;
        this.amount = amount;
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }
}