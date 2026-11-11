package store.service;

import store.model.*;
import store.repository.StoreRepository;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StoreService {
    private final StoreRepository repository;

    public StoreService(StoreRepository repository) {
        this.repository = repository;
    }

    public void addCategory(Category category) {
        if (repository.categories().putIfAbsent(category.getId(), category) != null) {
            throw new IllegalArgumentException("Duplicate category");
        }
    }

    public void addProduct(Product product) {
        if (repository.products().putIfAbsent(product.getId(), product) != null) {
            throw new IllegalArgumentException("Duplicate product identifier");
        }
        repository.stock().put(product.getId(), 0);
    }

    public Product product(String id) {
        Product product = repository.products().get(id);
        if (product == null) throw new IllegalArgumentException("Product not found: " + id);
        return product;
    }

    public void activate(String id) {
        product(id).setActive(true);
    }

    public void deactivate(String id) {
        product(id).setActive(false);
    }

    public List<Product> searchByName(String query) {
        return repository.products().values().stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Product> byCategory(String categoryId) {
        return repository.products().values().stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public void increaseStock(String id, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Negative stock change");
        product(id);
        repository.stock().put(id, repository.stock().get(id) + quantity);
    }

    public void decreaseStock(String id, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Negative stock change");
        product(id);
        if (repository.stock().get(id) < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        repository.stock().put(id, repository.stock().get(id) - quantity);
    }

    public int stock(String id) {
        product(id);
        return repository.stock().get(id);
    }

    public void addCustomer(Customer customer) {
        if (repository.customers().putIfAbsent(customer.getId(), customer) != null) {
            throw new IllegalArgumentException("Duplicate customer identifier");
        }
    }

    public void addSupplier(Supplier supplier) {
        if (repository.suppliers().putIfAbsent(supplier.getId(), supplier) != null) {
            throw new IllegalArgumentException("Duplicate supplier identifier");
        }
    }

    public void addPromotion(Promotion promotion) {
        repository.promotions().add(promotion);
    }

    public Cart createCart(String id, String customerId) {
        if (repository.carts().containsKey(id)) throw new IllegalArgumentException("Duplicate cart");
        Customer customer = repository.customers().get(customerId);
        if (customer == null) throw new IllegalArgumentException("Customer not found");
        Cart cart = new Cart(id, customer);
        repository.carts().put(id, cart);
        return cart;
    }

    public void addToCart(String cartId, String productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Cart quantity must be positive");
        Cart cart = repository.carts().get(cartId);
        if (cart == null) throw new IllegalArgumentException("Cart not found");

        Product product = product(productId);
        if (!product.isActive()) throw new IllegalStateException("Inactive product cannot be added");

        int newQuantity = cart.getItems().getOrDefault(productId, 0) + quantity;
        if (repository.stock().get(productId) < newQuantity) {
            throw new IllegalStateException("Insufficient stock for cart");
        }

        cart.put(productId, newQuantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Cart quantity must be positive");

        Cart cart = repository.carts().get(cartId);
        if (cart == null) throw new IllegalArgumentException("Cart not found");

        product(productId);

        if (!cart.getItems().containsKey(productId)) {
            throw new IllegalArgumentException("Product not in cart");
        }

        if (repository.stock().get(productId) < quantity) {
            throw new IllegalStateException("Insufficient stock for cart");
        }

        cart.put(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        Cart cart = repository.carts().get(cartId);
        if (cart == null) throw new IllegalArgumentException("Cart not found");
        cart.remove(productId);
    }

    public double cartSubtotal(String id) {
        Cart cart = repository.carts().get(id);
        if (cart == null) throw new IllegalArgumentException("Cart not found");
        return cart.subtotal(this::product);
    }

    public Order createOrder(String id, String cartId) {
        if (repository.orders().containsKey(id)) throw new IllegalArgumentException("Duplicate order");

        Cart cart = repository.carts().get(cartId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        List<Order.Item> items = new ArrayList<>();

        for (var entry : cart.getItems().entrySet()) {
            Product product = product(entry.getKey());
            decreaseStock(product.getId(), entry.getValue());
            items.add(new Order.Item(product, entry.getValue(), product.getUnitPrice()));
        }

        Order order = new Order(id, cart.getCustomer(), items);
        repository.orders().put(id, order);
        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = order(orderId);
        order.setPromotionDiscount(promotion.discount(order));
    }

    public void applyShipping(String orderId, ShippingRule shippingRule) {
        order(orderId).setShippingCost(shippingRule.getCost());
    }

    public Order order(String id) {
        Order order = repository.orders().get(id);
        if (order == null) throw new IllegalArgumentException("Order not found");
        return order;
    }

    public Payment pay(String paymentId, String orderId) {
        Order order = order(orderId);

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }

        if (order.getStatus() == Order.Status.PAID) {
            throw new IllegalStateException("Order already paid");
        }

        Payment payment = new Payment(paymentId, orderId, order.finalTotal());
        order.markPaid();
        repository.payments().add(payment);

        cash("CF-PAY-" + paymentId, CashflowEntry.Type.INFLOW, payment.getAmount(), "Payment " + paymentId);
        return payment;
    }

    public void cancel(String orderId) {
        Order order = order(orderId);
        order.cancel();

        for (Order.Item item : order.getItems()) {
            increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    public ReturnRequest createReturn(String id, String orderId) {
        Order order = order(orderId);

        if (order.getStatus() != Order.Status.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        if (repository.returns().containsKey(id)) {
            throw new IllegalArgumentException("Duplicate return");
        }

        ReturnRequest request = new ReturnRequest(id, order);
        repository.returns().put(id, request);
        return request;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        ReturnRequest request = repository.returns().get(returnId);
        if (request == null) throw new IllegalArgumentException("Return not found");

        Order.Item found = null;

        for (Order.Item item : request.getOrder().getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                found = item;
                break;
            }
        }

        if (found == null) throw new IllegalArgumentException("Product not in order");

        int alreadyReturned = request.getItems().getOrDefault(productId, 0);

        if (quantity <= 0 || alreadyReturned + quantity > found.getQuantity()) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        request.add(productId, quantity);
    }

    public double calculateRefund(String id) {
        ReturnRequest request = repository.returns().get(id);
        if (request == null) throw new IllegalArgumentException("Return not found");

        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Return must contain an item");
        }

        double total = 0;

        for (var entry : request.getItems().entrySet()) {
            for (Order.Item item : request.getOrder().getItems()) {
                if (item.getProduct().getId().equals(entry.getKey())) {
                    total += item.getUnitPrice() * entry.getValue();
                }
            }
        }

        request.setRefundTotal(total);
        return total;
    }

    public void processRefund(String id) {
        ReturnRequest request = repository.returns().get(id);
        if (request == null) throw new IllegalArgumentException("Return not found");
        if (request.isProcessed()) throw new IllegalStateException("Return already processed");

        double amount = calculateRefund(id);

        for (var entry : request.getItems().entrySet()) {
            increaseStock(entry.getKey(), entry.getValue());
        }

        request.getOrder().markRefunded();
        request.markProcessed();

        cash("CF-REF-" + id, CashflowEntry.Type.OUTFLOW, amount, "Refund " + id);
    }

    public Restock restock(String id, String productId, String supplierId, int quantity, double unitCost) {
        Product product = product(productId);
        Supplier supplier = repository.suppliers().get(supplierId);

        if (supplier == null) throw new IllegalArgumentException("Supplier not found");
        if (repository.restocks().containsKey(id)) throw new IllegalArgumentException("Duplicate restock");

        Restock restock = new Restock(id, product, supplier, quantity, unitCost);
        repository.restocks().put(id, restock);

        increaseStock(productId, quantity);
        cash("CF-REST-" + id, CashflowEntry.Type.OUTFLOW, restock.totalCost(), "Restock " + id);

        return restock;
    }

    public void cash(String id, CashflowEntry.Type type, double amount, String description) {
        repository.cashflows().add(new CashflowEntry(id, type, amount, description));
    }

    public double inflow() {
        return repository.cashflows().stream()
                .filter(x -> x.getType() == CashflowEntry.Type.INFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public double outflow() {
        return repository.cashflows().stream()
                .filter(x -> x.getType() == CashflowEntry.Type.OUTFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public void exportInventory(String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("productId,stock");
            for (var entry : repository.stock().entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportCashflow(String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,type,amount,description");

            for (CashflowEntry entry : repository.cashflows()) {
                writer.println(
                        entry.getId() + "," +
                        entry.getType() + "," +
                        entry.getAmount() + ",\"" +
                        entry.getDescription().replace("\"", "\"\"") + "\""
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public StoreRepository repository() {
        return repository;
    }
}