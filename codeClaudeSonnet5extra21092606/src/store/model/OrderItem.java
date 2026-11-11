package store.model;

import store.exception.InvalidOperationException;
import store.exception.InvalidQuantityException;

public class OrderItem {
    private final Product product;
    private final int quantity;
    private final double unitPriceAtOrderTime;
    private int returnedQuantity;

    public OrderItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Order item quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtOrderTime = product.getUnitPrice();
        this.returnedQuantity = 0;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPriceAtOrderTime() {
        return unitPriceAtOrderTime;
    }

    public double getSubtotal() {
        return quantity * unitPriceAtOrderTime;
    }

    public int getReturnedQuantity() {
        return returnedQuantity;
    }

    public int getRemainingReturnableQuantity() {
        return quantity - returnedQuantity;
    }

    public void registerReturn(int returnQuantity) {
        if (returnQuantity <= 0) {
            throw new InvalidQuantityException("Return quantity must be positive");
        }
        if (returnQuantity > getRemainingReturnableQuantity()) {
            throw new InvalidOperationException(
                    "Returned quantity exceeds remaining returnable quantity for product " + product.getId());
        }
        this.returnedQuantity += returnQuantity;
    }
}