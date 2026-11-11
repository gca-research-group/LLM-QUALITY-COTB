package store.repository;

import store.exception.BusinessRuleException;
import store.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();

    public void add(Product product) {
        if (products.containsKey(product.getId())) {
            throw new BusinessRuleException("Duplicate product identifier: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    public Product findById(String id) {
        return products.get(id);
    }

    public List<Product> findByName(String name) {
        List<Product> result = new ArrayList<>();
        String lowerName = name.toLowerCase();
        for (Product product : products.values()) {
            if (product.getName().toLowerCase().contains(lowerName)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> findByCategory(String categoryId) {
        List<Product> result = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }
        return result;
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public boolean exists(String id) {
        return products.containsKey(id);
    }
}