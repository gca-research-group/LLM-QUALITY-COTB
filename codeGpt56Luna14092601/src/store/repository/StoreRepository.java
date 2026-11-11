package store.repository;

import store.model.*;
import java.util.*;

public class StoreRepository {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Category> categories = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, Restock> restocks = new LinkedHashMap<>();
    private final List<Promotion> promotions = new ArrayList<>();
    private final List<CashflowEntry> cashflows = new ArrayList<>();
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final List<Payment> payments = new ArrayList<>();

    public Map<String, Product> products() { return products; }
    public Map<String, Category> categories() { return categories; }
    public Map<String, Customer> customers() { return customers; }
    public Map<String, Supplier> suppliers() { return suppliers; }
    public Map<String, Cart> carts() { return carts; }
    public Map<String, Order> orders() { return orders; }
    public Map<String, ReturnRequest> returns() { return returns; }
    public Map<String, Restock> restocks() { return restocks; }
    public List<Promotion> promotions() { return promotions; }
    public List<CashflowEntry> cashflows() { return cashflows; }
    public Map<String, Integer> stock() { return stock; }
    public List<Payment> payments() { return payments; }
}