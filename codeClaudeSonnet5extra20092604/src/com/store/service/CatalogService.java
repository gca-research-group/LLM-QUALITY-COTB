package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.model.Category;
import com.store.model.Product;
import com.store.repository.CategoryRepository;
import com.store.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class CatalogService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public CatalogService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void registerCategory(Category category) {
        categoryRepository.add(category);
    }

    public void registerProduct(Product product) {
        categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new StoreOperationException("Category not registered: " + product.getCategory().getId()));
        productRepository.add(product);
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    public Product getProductOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new StoreOperationException("Product not found: " + id));
    }

    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> listByCategory(String categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public void activateProduct(String id) {
        getProductOrThrow(id).activate();
    }

    public void deactivateProduct(String id) {
        getProductOrThrow(id).deactivate();
    }
}