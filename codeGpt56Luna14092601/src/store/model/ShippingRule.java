package store.model;

public class ShippingRule {
    public enum Type { STANDARD, EXPRESS, PICKUP }

    private final Type type;
    private final double cost;

    public ShippingRule(Type type, double cost) {
        if (type == null || cost < 0 || (type == Type.PICKUP && cost != 0)) throw new IllegalArgumentException("Invalid shipping rule");
        this.type = type;
        this.cost = cost;
    }

    public Type getType() { return type; }
    public double getCost() { return cost; }
}