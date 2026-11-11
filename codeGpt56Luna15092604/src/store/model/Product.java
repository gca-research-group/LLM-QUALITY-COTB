package store.model;

public class Product {
    private final String id;
    private final String name;
    private final double unitPrice;
    private final Category category;
    private boolean active;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null || id.isBlank() || name == null || name.isBlank() || unitPrice < 0 || category == null) {
            throw new IllegalArgumentException("Invalid product");
        }
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public Category getCategory() { return category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}