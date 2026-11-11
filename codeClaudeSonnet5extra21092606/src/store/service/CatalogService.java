package store.service;

import store.model.Category;
import store.model.Product;
import store.repository.CategoryRepository;
import store.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

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

    public Product findProductById(String productId) {
        return productRepository.findById(productId);
    }

    public List<Product> findProductsByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findProductsByCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId);
        return productRepository.findByCategory(category);
    }

    public void activateProduct(String productId) {
        productRepository.findById(productId).activate();
    }

    public void deactivateProduct(String productId) {
        productRepository.findById(productId).deactivate();
    }

    public List<Product> allProducts() {
        return new ArrayList<>(productRepository.findAll());
    }

    public int productCount() {
        return productRepository.findAll().size();
    }
}