package store.model;

public class ReturnItem {
    private final Product product;
    private final int quantity;
    private final double unitPrice;

    public ReturnItem(Product product, int quantity, double unitPrice) {
        if (product == null || quantity <= 0 || unitPrice < 0) {
            throw new IllegalArgumentException("Invalid return item");
        }

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getRefund() {
        return quantity * unitPrice;
    }
}