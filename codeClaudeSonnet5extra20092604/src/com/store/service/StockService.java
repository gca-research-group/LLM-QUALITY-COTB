package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.model.Product;
import com.store.repository.ProductRepository;

public class StockService {
    private final ProductRepository productRepository;

    public StockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void increaseStock(String productId, int quantity) {
        getProduct(productId).increaseStock(quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        getProduct(productId).decreaseStock(quantity);
    }

    public int getStock(String productId) {
        return getProduct(productId).getStock();
    }

    private Product getProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new StoreOperationException("Product not found: " + productId));
    }
}