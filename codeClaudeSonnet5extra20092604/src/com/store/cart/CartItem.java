package com.store.cart;

import com.store.exception.StoreOperationException;
import com.store.model.Product;

public class CartItem {
    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        setQuantity(quantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new StoreOperationException("Cart item quantity must be positive");
        }
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return quantity * product.getUnitPrice();
    }
}