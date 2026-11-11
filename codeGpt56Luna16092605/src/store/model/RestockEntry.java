package store.model;

public class RestockEntry {
    private final String id;
    private final String productId;
    private final String supplierId;
    private final int quantity;
    private final double unitCost;

    public RestockEntry(String id, String productId, String supplierId,
                        int quantity, double unitCost) {
        if (id == null || productId == null || supplierId == null
                || quantity <= 0 || unitCost < 0) {
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

    public double getTotalCost() {
        return quantity * unitCost;
    }
}