package store.model;

import store.exception.InvalidQuantityException;

public class ReturnItem {
    private final Product product;
    private final int quantity;

    public ReturnItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Returned quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}