package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.Category;
import store.model.Product;
import store.repository.InMemoryRepository;

public class CatalogService {
    private final InMemoryRepository<Category> categories =
        new InMemoryRepository<Category>(new InMemoryRepository.IdProvider<Category>() {
            public String getId(Category value) {
                return value.getId();
            }
        });

    private final InMemoryRepository<Product> products =
        new InMemoryRepository<Product>(new InMemoryRepository.IdProvider<Product>() {
            public String getId(Product value) {
                return value.getId();
            }
        });

    public void addCategory(Category category) {
        categories.save(category);
    }

    public void addProduct(Product product) {
        if (categories.findById(product.getCategory().getId()) == null) {
            throw new IllegalArgumentException("Unknown category");
        }

        products.save(product);
    }

    public Product findProductById(String id) {
        return products.findById(id);
    }

    public List<Product> searchByName(String name) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : products.findAll()) {
            if (product.getName().toLowerCase().indexOf(name.toLowerCase()) >= 0) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> listByCategory(String categoryId) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : products.findAll()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }

        return result;
    }

    public void activate(String id) {
        require(id).setActive(true);
    }

    public void deactivate(String id) {
        require(id).setActive(false);
    }

    public Product require(String id) {
        Product product = findProductById(id);

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }

        return product;
    }

    public int productCount() {
        return products.count();
    }

    public java.util.Collection<Product> allProducts() {
        return products.findAll();
    }
}