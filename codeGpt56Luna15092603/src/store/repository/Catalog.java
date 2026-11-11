package store.repository;

import store.model.Category;
import store.model.Product;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Catalog {
    private final Map<String, Category> categories = new LinkedHashMap<>();
    private final Map<String, Product> products = new LinkedHashMap<>();

    public void addCategory(Category category) {
        if (categories.putIfAbsent(category.getId(), category) != null) {
            throw new IllegalArgumentException("Duplicate category identifier");
        }
    }

    public void addProduct(Product product) {
        if (products.putIfAbsent(product.getId(), product) != null) {
            throw new IllegalArgumentException("Duplicate product identifier");
        }
    }

    public Product findProduct(String id) {
        return products.get(id);
    }

    public List<Product> searchByName(String name) {
        String value = name.toLowerCase();

        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(value))
                .collect(Collectors.toList());
    }

    public List<Product> byCategory(String categoryId) {
        return products.values().stream()
                .filter(product -> product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public Collection<Product> products() {
        return Collections.unmodifiableCollection(products.values());
    }

    public int productCount() {
        return products.size();
    }
}