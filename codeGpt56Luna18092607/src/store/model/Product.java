package store.model;

public class Product {
    private String id;
    private String name;
    private double unitPrice;
    private Category category;
    private boolean active;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null || id.length() == 0 || name == null || name.length() == 0 || unitPrice < 0 || category == null) {
            throw new IllegalArgumentException("Invalid product");
        }
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}