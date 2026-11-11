package store.repository;

import store.model.Category;
import store.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository extends InMemoryRepository<Product> {

    public ProductRepository() {
        super("Product");
    }

    @Override
    protected String extractId(Product entity) {
        return entity.getId();
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        for (Product product : findAll()) {
            if (product.getName() != null && product.getName().equalsIgnoreCase(name)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> findByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : findAll()) {
            if (product.getCategory().getId().equals(category.getId())) {
                result.add(product);
            }
        }
        return result;
    }
}