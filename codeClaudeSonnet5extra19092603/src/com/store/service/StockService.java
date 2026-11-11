package com.store.service;

import com.store.model.Product;

public class StockService {
    private final CatalogService catalogService;

    public StockService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void increaseStock(String productId, int quantity) {
        Product product = catalogService.requireProduct(productId);
        product.increaseStock(quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        Product product = catalogService.requireProduct(productId);
        product.decreaseStock(quantity);
    }

    public int getStock(String productId) {
        return catalogService.requireProduct(productId).getStockQuantity();
    }
}