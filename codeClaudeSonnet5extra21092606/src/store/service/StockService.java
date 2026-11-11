package store.service;

import store.model.Product;
import store.repository.ProductRepository;

public class StockService {
    private final ProductRepository productRepository;

    public StockService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void increaseStock(String productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.increaseStock(quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        Product product = productRepository.findById(productId);
        product.decreaseStock(quantity);
    }

    public int getStock(String productId) {
        return productRepository.findById(productId).getStockQuantity();
    }
}