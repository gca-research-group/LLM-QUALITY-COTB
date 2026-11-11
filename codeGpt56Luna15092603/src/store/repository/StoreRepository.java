package store.repository;

import store.model.CashflowEntry;
import store.model.Cart;
import store.model.Customer;
import store.model.Order;
import store.model.Payment;
import store.model.Product;
import store.model.Promotion;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.ShippingRule;
import store.model.Supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoreRepository {
    private final Catalog catalog = new Catalog();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, Payment> payments = new LinkedHashMap<>();
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, Restock> restocks = new LinkedHashMap<>();
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();
    private final Map<String, ShippingRule> shippingRules = new LinkedHashMap<>();
    private final List<CashflowEntry> cashflows = new ArrayList<>();

    public Catalog getCatalog() {
        return catalog;
    }

    public void addCustomer(Customer customer) {
        if (customers.putIfAbsent(customer.getId(), customer) != null) {
            throw new IllegalArgumentException("Duplicate customer identifier");
        }
    }

    public void addSupplier(Supplier supplier) {
        if (suppliers.putIfAbsent(supplier.getId(), supplier) != null) {
            throw new IllegalArgumentException("Duplicate supplier identifier");
        }
    }

    public Customer customer(String id) {
        return customers.get(id);
    }

    public Supplier supplier(String id) {
        return suppliers.get(id);
    }

    public void addCart(Cart cart) {
        if (carts.putIfAbsent(cart.getId(), cart) != null) {
            throw new IllegalArgumentException("Duplicate cart identifier");
        }
    }

    public Cart cart(String id) {
        return carts.get(id);
    }

    public void addOrder(Order order) {
        if (orders.putIfAbsent(order.getId(), order) != null) {
            throw new IllegalArgumentException("Duplicate order identifier");
        }
    }

    public Order order(String id) {
        return orders.get(id);
    }

    public void addPayment(Payment payment) {
        if (payments.putIfAbsent(payment.getId(), payment) != null) {
            throw new IllegalArgumentException("Duplicate payment identifier");
        }
    }

    public void addReturn(ReturnRequest returnRequest) {
        if (returns.putIfAbsent(returnRequest.getId(), returnRequest) != null) {
            throw new IllegalArgumentException("Duplicate return identifier");
        }
    }

    public ReturnRequest returnRequest(String id) {
        return returns.get(id);
    }

    public void addRestock(Restock restock) {
        if (restocks.putIfAbsent(restock.getId(), restock) != null) {
            throw new IllegalArgumentException("Duplicate restock identifier");
        }
    }

    public void addPromotion(String id, Promotion promotion) {
        if (promotions.putIfAbsent(id, promotion) != null) {
            throw new IllegalArgumentException("Duplicate promotion identifier");
        }
    }

    public Promotion promotion(String id) {
        return promotions.get(id);
    }

    public void addShippingRule(String id, ShippingRule rule) {
        if (shippingRules.putIfAbsent(id, rule) != null) {
            throw new IllegalArgumentException("Duplicate shipping rule identifier");
        }
    }

    public ShippingRule shippingRule(String id) {
        return shippingRules.get(id);
    }

    public int stock(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    public void setInitialStock(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        stock.put(productId, quantity);
    }

    public void increaseStock(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        stock.put(productId, stock(productId) + quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        if (quantity < 0 || stock(productId) < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        stock.put(productId, stock(productId) - quantity);
    }

    public void addCashflow(CashflowEntry entry) {
        cashflows.add(entry);
    }

    public Collection<Customer> customers() {
        return Collections.unmodifiableCollection(customers.values());
    }

    public Collection<Supplier> suppliers() {
        return Collections.unmodifiableCollection(suppliers.values());
    }

    public Collection<Order> orders() {
        return Collections.unmodifiableCollection(orders.values());
    }

    public Collection<ReturnRequest> returns() {
        return Collections.unmodifiableCollection(returns.values());
    }

    public Collection<Restock> restocks() {
        return Collections.unmodifiableCollection(restocks.values());
    }

    public List<CashflowEntry> cashflows() {
        return Collections.unmodifiableList(cashflows);
    }
}