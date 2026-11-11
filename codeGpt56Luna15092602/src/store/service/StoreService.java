package store.service;

import store.model.Cart;
import store.model.Category;
import store.model.Customer;
import store.model.Order;
import store.model.Product;
import store.model.Promotion;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.Supplier;
import store.repository.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StoreService {
    private final Repository<Category> categories = new Repository<>();
    private final Repository<Product> products = new Repository<>();
    private final Repository<Customer> customers = new Repository<>();
    private final Repository<Supplier> suppliers = new Repository<>();
    private final Repository<Cart> carts = new Repository<>();
    private final Repository<Order> orders = new Repository<>();
    private final Repository<ReturnRequest> returns = new Repository<>();
    private final Repository<Restock> restocks = new Repository<>();

    private final InventoryService inventory = new InventoryService();
    private final CashflowService cashflow = new CashflowService();
    private final List<Promotion> promotions = new ArrayList<>();

    public void addCategory(Category category) {
        categories.add(category.getId(), category);
    }

    public void addProduct(Product product, int initialStock) {
        products.add(product.getId(), product);
        inventory.initialize(product, initialStock);
    }

    public Product product(String id) {
        Product product = products.get(id);

        if (product == null) {
            throw new IllegalArgumentException("Unknown product: " + id);
        }

        return product;
    }

    public List<Product> searchName(String name) {
        return products.all()
                .stream()
                .filter(product -> product.getName()
                        .toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> byCategory(String categoryId) {
        return products.all()
                .stream()
                .filter(product -> product.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public void activate(String id) {
        product(id).setActive(true);
    }

    public void deactivate(String id) {
        product(id).setActive(false);
    }

    public void registerCustomer(Customer customer) {
        customers.add(customer.getId(), customer);
    }

    public void registerSupplier(Supplier supplier) {
        suppliers.add(supplier.getId(), supplier);
    }

    public Customer customer(String id) {
        Customer customer = customers.get(id);

        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer: " + id);
        }

        return customer;
    }

    public Supplier supplier(String id) {
        Supplier supplier = suppliers.get(id);

        if (supplier == null) {
            throw new IllegalArgumentException("Unknown supplier: " + id);
        }

        return supplier;
    }

    public Cart createCart(String id, String customerId) {
        Cart cart = new Cart(id, customer(customerId));
        carts.add(id, cart);
        return cart;
    }

    public Cart cart(String id) {
        Cart cart = carts.get(id);

        if (cart == null) {
            throw new IllegalArgumentException("Unknown cart: " + id);
        }

        return cart;
    }

    public void addToCart(String cartId, String productId, int quantity) {
        cart(cartId).add(product(productId), quantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        cart(cartId).update(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        cart(cartId).remove(productId);
    }

    public void addPromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    public Order createOrder(String id, String cartId) {
        Cart cart = cart(cartId);

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot create empty order");
        }

        List<Order.OrderItem> items = cart.getItems()
                .stream()
                .map(item -> new Order.OrderItem(
                        item.getProduct(),
                        item.getQuantity(),
                        item.getProduct().getUnitPrice()
                ))
                .collect(Collectors.toList());

        for (Order.OrderItem item : items) {
            inventory.decrease(item.getProduct(), item.getQuantity());
        }

        Order order = new Order(id, cart.getCustomer(), items);
        orders.add(id, order);

        return order;
    }

    public Order order(String id) {
        Order order = orders.get(id);

        if (order == null) {
            throw new IllegalArgumentException("Unknown order: " + id);
        }

        return order;
    }

    public double applyPromotion(String orderId, Promotion promotion) {
        Order order = order(orderId);
        double discount = promotion.discount(order);
        order.setPromotionDiscount(discount);
        return discount;
    }

    public double applyFirstEligiblePromotion(String orderId) {
        Order order = order(orderId);
        double best = 0;

        for (Promotion promotion : promotions) {
            best = Math.max(best, promotion.discount(order));
        }

        order.setPromotionDiscount(best);
        return best;
    }

    public double applyShipping(String orderId, store.model.ShippingRule rule) {
        Order order = order(orderId);
        double cost = rule.cost(order);
        order.setShippingCost(cost);
        return cost;
    }

    public store.model.Payment pay(String orderId, String paymentId) {
        Order order = order(orderId);
        double amount = order.getFinalTotal();

        order.markPaid();

        store.model.Payment payment = new store.model.Payment(
                paymentId,
                orderId,
                amount
        );

        cashflow.inflow(amount, "Payment " + paymentId + " for " + orderId);

        return payment;
    }

    public void cancel(String orderId) {
        Order order = order(orderId);

        order.cancel();

        for (Order.OrderItem item : order.getItems()) {
            inventory.increase(item.getProduct(), item.getQuantity());
        }
    }

    public ReturnRequest createReturn(String id, String orderId) {
        ReturnRequest request = new ReturnRequest(id, order(orderId));
        returns.add(id, request);
        return request;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        ReturnRequest request = returns.get(returnId);

        if (request == null) {
            throw new IllegalArgumentException("Unknown return");
        }

        request.add(product(productId), quantity);
    }

    public double processReturn(String returnId) {
        ReturnRequest request = returns.get(returnId);

        if (request == null) {
            throw new IllegalArgumentException("Unknown return");
        }

        if (request.isProcessed()) {
            throw new IllegalStateException("Return already processed");
        }

        double total = request.getRefundTotal();

        for (ReturnRequest.ReturnedItem item : request.getItems()) {
            inventory.increase(item.getProduct(), item.getQuantity());
        }

        request.markProcessed();
        request.getOrder().markRefunded();

        cashflow.outflow(total, "Refund " + returnId);

        return total;
    }

    public Restock restock(
            String id,
            String productId,
            String supplierId,
            int quantity,
            double unitCost
    ) {
        Restock restock = new Restock(
                id,
                product(productId),
                supplier(supplierId),
                quantity,
                unitCost
        );

        inventory.increase(restock.getProduct(), quantity);
        restocks.add(id, restock);
        cashflow.outflow(restock.totalCost(), "Restock " + id);

        return restock;
    }

    public void manualOutflow(double amount, String description) {
        cashflow.manualOutflow(amount, description);
    }

    public int stock(String productId) {
        return inventory.get(product(productId));
    }

    public int productCount() {
        return products.size();
    }

    public int customerCount() {
        return customers.size();
    }

    public int supplierCount() {
        return suppliers.size();
    }

    public Collection<Order> orders() {
        return orders.all();
    }

    public Collection<ReturnRequest> returns() {
        return returns.all();
    }

    public Collection<Restock> restocks() {
        return restocks.all();
    }

    public CashflowService cashflow() {
        return cashflow;
    }

    public Collection<Product> products() {
        return products.all();
    }

    public void exportInventory(String file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("productId,name,stock");

            for (Product product : products.all()) {
                writer.printf(
                        Locale.US,
                        "%s,%s,%d%n",
                        product.getId(),
                        product.getName(),
                        inventory.get(product)
                );
            }
        }
    }

    public void exportCashflow(String file) throws IOException {
        cashflow.exportCsv(file);
    }
}