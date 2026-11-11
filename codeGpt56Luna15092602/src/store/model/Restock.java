package store.model;

public class Restock {
    private final String id;
    private final Product product;
    private final Supplier supplier;
    private final int quantity;
    private final double unitCost;

    public Restock(String id, Product product, Supplier supplier, int quantity, double unitCost) {
        if (quantity <= 0 || unitCost < 0) {
            throw new IllegalArgumentException("Invalid restock");
        }

        this.id = id;
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public double totalCost() {
        return quantity * unitCost;
    }
}