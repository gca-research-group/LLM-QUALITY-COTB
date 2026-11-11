package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EntityNotFoundException;
import com.store.model.Category;
import com.store.model.Product;
import com.store.repository.CategoryRepository;
import com.store.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CatalogService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public CatalogService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(Category category) {
        if (categoryRepository.existsById(category.getId())) {
            throw new DuplicateIdException("Duplicate category id: " + category.getId());
        }
        categoryRepository.save(category);
    }

    public void addProduct(Product product) {
        if (productRepository.existsById(product.getId())) {
            throw new DuplicateIdException("Duplicate product id: " + product.getId());
        }
        productRepository.save(product);
    }

    public Product findById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        String lowered = name.toLowerCase();
        for (Product product : productRepository.findAll()) {
            if (product.getName().toLowerCase().contains(lowered)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> listByCategory(String categoryId) {
        List<Product> result = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }
        return result;
    }

    public void activateProduct(String productId) {
        findById(productId).activate();
    }

    public void deactivateProduct(String productId) {
        findById(productId).deactivate();
    }

    public Collection<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public int countProducts() {
        return productRepository.findAll().size();
    }
}