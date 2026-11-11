package com.store.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.store.exception.NotFoundException;
import com.store.model.Category;
import com.store.model.Product;
import com.store.repository.CategoryRepository;
import com.store.repository.ProductRepository;

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
        productRepository.add(product);
    }

    public Optional<Product> findProductById(String id) {
        return productRepository.findById(id);
    }

    public Product requireProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    public Category requireCategory(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
    }

    public List<Product> findProductsByName(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (product.getName().equalsIgnoreCase(name)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> findProductsByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (product.getCategory().getId().equals(category.getId())) {
                result.add(product);
            }
        }
        return result;
    }

    public void activateProduct(String id) {
        requireProduct(id).activate();
    }

    public void deactivateProduct(String id) {
        requireProduct(id).deactivate();
    }

    public Collection<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Collection<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
}