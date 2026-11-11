package store.repository;

import java.util.*;
import store.model.*;

public class StoreRepository {
    private final Map<String, Category> categories = new LinkedHashMap<>();
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, Restock> restocks = new LinkedHashMap<>();
    private final Map<String, CashflowEntry> cashflows = new LinkedHashMap<>();
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final List<Promotion> promotions = new ArrayList<>();

    public Map<String, Category> getCategories() { return categories; }
    public Map<String, Product> getProducts() { return products; }
    public Map<String, Customer> getCustomers() { return customers; }
    public Map<String, Supplier> getSuppliers() { return suppliers; }
    public Map<String, Cart> getCarts() { return carts; }
    public Map<String, Order> getOrders() { return orders; }
    public Map<String, Payment> getPayments() { return payments; }
    public Map<String, ReturnRequest> getReturns() { return returns; }
    public Map<String, Restock> getRestocks() { return restocks; }
    public Map<String, CashflowEntry> getCashflows() { return cashflows; }
    public Map<String, Integer> getStock() { return stock; }
    public List<Promotion> getPromotions() { return promotions; }
}