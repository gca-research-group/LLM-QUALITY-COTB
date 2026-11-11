package store.model;

public class Restock {
    private final String id;
    private final String productId;
    private final String supplierId;
    private final int quantity;
    private final double unitCost;

    public Restock(String id, String productId, String supplierId, int quantity, double unitCost) {
        if (id == null || id.isBlank() || productId == null || productId.isBlank()
                || supplierId == null || supplierId.isBlank() || quantity <= 0 || unitCost < 0) {
            throw new IllegalArgumentException("Invalid restock");
        }

        this.id = id;
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getSupplierId() {
        return supplierId;
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