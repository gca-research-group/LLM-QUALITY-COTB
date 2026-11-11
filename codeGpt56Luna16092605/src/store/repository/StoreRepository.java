package store.repository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import store.model.Cart;
import store.model.CashflowEntry;
import store.model.Category;
import store.model.Customer;
import store.model.Order;
import store.model.Payment;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.model.Supplier;

public class StoreRepository {
    private final Map<String, Category> categories = new LinkedHashMap<String, Category>();
    private final Map<String, Product> products = new LinkedHashMap<String, Product>();
    private final Map<String, Customer> customers = new LinkedHashMap<String, Customer>();
    private final Map<String, Supplier> suppliers = new LinkedHashMap<String, Supplier>();
    private final Map<String, Cart> carts = new LinkedHashMap<String, Cart>();
    private final Map<String, Order> orders = new LinkedHashMap<String, Order>();
    private final Map<String, Payment> payments = new LinkedHashMap<String, Payment>();
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<String, ReturnRequest>();
    private final Map<String, RestockEntry> restocks = new LinkedHashMap<String, RestockEntry>();
    private final Map<String, CashflowEntry> cashflows = new LinkedHashMap<String, CashflowEntry>();

    public void addCategory(Category category) {
        ensureUnique(categories, category.getId());
        categories.put(category.getId(), category);
    }

    public Category category(String id) {
        return categories.get(id);
    }

    public Collection<Category> categories() {
        return categories.values();
    }

    public void addProduct(Product product) {
        ensureUnique(products, product.getId());
        products.put(product.getId(), product);
    }

    public Product product(String id) {
        return products.get(id);
    }

    public Collection<Product> products() {
        return products.values();
    }

    public void addCustomer(Customer customer) {
        ensureUnique(customers, customer.getId());
        customers.put(customer.getId(), customer);
    }

    public Customer customer(String id) {
        return customers.get(id);
    }

    public Collection<Customer> customers() {
        return customers.values();
    }

    public void addSupplier(Supplier supplier) {
        ensureUnique(suppliers, supplier.getId());
        suppliers.put(supplier.getId(), supplier);
    }

    public Supplier supplier(String id) {
        return suppliers.get(id);
    }

    public Collection<Supplier> suppliers() {
        return suppliers.values();
    }

    public void addCart(Cart cart) {
        ensureUnique(carts, cart.getId());
        carts.put(cart.getId(), cart);
    }

    public Cart cart(String id) {
        return carts.get(id);
    }

    public Collection<Cart> carts() {
        return carts.values();
    }

    public void addOrder(Order order) {
        ensureUnique(orders, order.getId());
        orders.put(order.getId(), order);
    }

    public Order order(String id) {
        return orders.get(id);
    }

    public Collection<Order> orders() {
        return orders.values();
    }

    public void addPayment(Payment payment) {
        ensureUnique(payments, payment.getId());
        payments.put(payment.getId(), payment);
    }

    public Collection<Payment> payments() {
        return payments.values();
    }

    public void addReturn(ReturnRequest returnRequest) {
        ensureUnique(returns, returnRequest.getId());
        returns.put(returnRequest.getId(), returnRequest);
    }

    public Collection<ReturnRequest> returns() {
        return returns.values();
    }

    public void addRestock(RestockEntry restockEntry) {
        ensureUnique(restocks, restockEntry.getId());
        restocks.put(restockEntry.getId(), restockEntry);
    }

    public Collection<RestockEntry> restocks() {
        return restocks.values();
    }

    public void addCashflow(CashflowEntry entry) {
        ensureUnique(cashflows, entry.getId());
        cashflows.put(entry.getId(), entry);
    }

    public Collection<CashflowEntry> cashflows() {
        return cashflows.values();
    }

    private void ensureUnique(Map<?, ?> map, String id) {
        if (map.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate identifier: " + id);
        }
    }
}