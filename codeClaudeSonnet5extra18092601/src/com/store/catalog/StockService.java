package com.store.catalog;

public class StockService {
    private final Catalog catalog;

    public StockService(Catalog catalog) {
        this.catalog = catalog;
    }

    public void increaseStock(String productId, int quantity) {
        Product product = catalog.findById(productId);
        product.increaseStock(quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        Product product = catalog.findById(productId);
        product.decreaseStock(quantity);
    }

    public int getStock(String productId) {
        return catalog.findById(productId).getStockQuantity();
    }
}