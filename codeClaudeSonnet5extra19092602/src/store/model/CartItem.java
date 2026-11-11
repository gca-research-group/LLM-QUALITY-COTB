package store.model;

import store.exception.BusinessRuleException;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Cart item quantity must be positive for product " + product.getId());
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

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Cart item quantity must be positive for product " + product.getId());
        }
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return quantity * product.getUnitPrice();
    }
}