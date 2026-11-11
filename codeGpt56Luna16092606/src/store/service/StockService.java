package store.service;

import store.model.Product;

public class StockService {
    public void initialize(Product product, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Negative stock");
        }

        if (product.getStock() != 0) {
            throw new IllegalStateException("Stock already initialized");
        }

        if (quantity > 0) {
            product.increaseStock(quantity);
        }
    }

    public void increase(Product product, int quantity) {
        product.increaseStock(quantity);
    }

    public void decrease(Product product, int quantity) {
        product.decreaseStock(quantity);
    }
}