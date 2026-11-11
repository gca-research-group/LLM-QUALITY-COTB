package store.service;

import store.exception.BusinessRuleException;
import store.model.Category;
import store.model.Product;
import store.repository.CategoryRepository;
import store.repository.ProductRepository;

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

    public Category findCategory(String categoryId) {
        Category category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new BusinessRuleException("Category not found: " + categoryId);
        }
        return category;
    }

    public void registerProduct(Product product) {
        productRepository.add(product);
    }

    public Product findProduct(String productId) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new BusinessRuleException("Product not found: " + productId);
        }
        return product;
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> listByCategory(String categoryId) {
        return productRepository.findByCategory(categoryId);
    }

    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    public void activateProduct(String productId) {
        findProduct(productId).activate();
    }

    public void deactivateProduct(String productId) {
        findProduct(productId).deactivate();
    }

    public void increaseStock(String productId, int quantity) {
        findProduct(productId).increaseStock(quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        findProduct(productId).decreaseStock(quantity);
    }

    public int totalProductCount() {
        return productRepository.findAll().size();
    }
}