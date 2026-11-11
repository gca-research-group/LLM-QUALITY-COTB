package store.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import store.model.Cart;
import store.model.CartItem;
import store.model.CashflowEntry;
import store.model.CashflowType;
import store.model.Customer;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Payment;
import store.model.Product;
import store.model.Promotion;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.model.Supplier;
import store.repository.StoreRepository;
import store.model.Category;
import store.model.ShippingRule;

public class StoreService {
    private final StoreRepository repository;
    private final List<Promotion> promotions = new ArrayList<Promotion>();

    public StoreService(StoreRepository repository) {
        this.repository = repository;
    }

    public void registerCategory(Category category) {
        repository.addCategory(category);
    }

    public void registerProduct(Product product) {
        repository.addProduct(product);
    }

    public void registerCustomer(Customer customer) {
        repository.addCustomer(customer);
    }

    public void registerSupplier(Supplier supplier) {
        repository.addSupplier(supplier);
    }

    public void registerPromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Invalid promotion");
        }
        promotions.add(promotion);
    }

    public List<Promotion> promotions() {
        return new ArrayList<Promotion>(promotions);
    }

    public Product findProductById(String id) {
        return requireProduct(id);
    }

    public List<Product> findProductsByName(String name) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : repository.products()) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> findProductsByCategory(String categoryId) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : repository.products()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }

        return result;
    }

    public void activateProduct(String id) {
        requireProduct(id).setActive(true);
    }

    public void deactivateProduct(String id) {
        requireProduct(id).setActive(false);
    }

    public void increaseStock(String id, int quantity) {
        requireProduct(id).increaseStock(quantity);
    }

    public void decreaseStock(String id, int quantity) {
        requireProduct(id).decreaseStock(quantity);
    }

    public Cart createCart(String id, String customerId) {
        Cart cart = new Cart(id, requireCustomer(customerId));
        repository.addCart(cart);
        return cart;
    }

    public void addToCart(String cartId, String productId, int quantity) {
        Cart cart = requireCart(cartId);
        Product product = requireProduct(productId);

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        cart.add(product, quantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        requireCart(cartId).update(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        requireCart(cartId).remove(productId);
    }

    public double cartSubtotal(String cartId) {
        return requireCart(cartId).getSubtotal();
    }

    public Order createOrder(String orderId, String cartId) {
        Cart cart = requireCart(cartId);

        if (cart.getItems().size() == 0) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        List<OrderItem> items = new ArrayList<OrderItem>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for " + product.getId());
            }

            items.add(new OrderItem(
                    product,
                    cartItem.getQuantity(),
                    product.getUnitPrice()));
        }

        for (CartItem cartItem : cart.getItems()) {
            cartItem.getProduct().decreaseStock(cartItem.getQuantity());
        }

        Order order = new Order(orderId, cart.getCustomer(), items);
        repository.addOrder(order);

        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = requireOrder(orderId);
        order.setPromotionDiscount(promotion.calculateDiscount(order));
    }

    public void applyShipping(String orderId, ShippingRule shippingRule) {
        Order order = requireOrder(orderId);
        order.setShippingCost(shippingRule.calculate(order));
    }

    public Payment payOrder(String paymentId, String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled order cannot be paid");
        }

        double amount = order.getFinalTotal();
        Payment payment = new Payment(paymentId, orderId, amount);

        order.markPaid();
        repository.addPayment(payment);

        recordCashflow(
                "CF-PAY-" + paymentId,
                CashflowType.INFLOW,
                amount,
                "Payment " + paymentId);

        return payment;
    }

    public void cancelOrder(String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalArgumentException("Paid order cannot be cancelled");
        }

        for (OrderItem item : order.getItems()) {
            item.getProduct().increaseStock(item.getQuantity());
        }

        order.cancel();
    }

    public ReturnRequest createReturn(String id, String orderId) {
        ReturnRequest returnRequest =
                new ReturnRequest(id, requireOrder(orderId));

        repository.addReturn(returnRequest);

        return returnRequest;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        requireReturn(returnId).addItem(productId, quantity);
    }

    public double refundTotal(String returnId) {
        return requireReturn(returnId).calculateRefundTotal();
    }

    public void processRefund(String returnId) {
        ReturnRequest returnRequest = requireReturn(returnId);

        if (returnRequest.isProcessed()) {
            throw new IllegalArgumentException("Return already processed");
        }

        double amount = returnRequest.calculateRefundTotal();

        for (Map.Entry<String, Integer> entry
                : returnRequest.getQuantities().entrySet()) {
            requireProduct(entry.getKey())
                    .increaseStock(entry.getValue().intValue());
        }

        returnRequest.markProcessed();

        recordCashflow(
                "CF-REF-" + returnRequest.getId(),
                CashflowType.OUTFLOW,
                amount,
                "Refund " + returnRequest.getId());
    }

    public RestockEntry restock(
            String id,
            String productId,
            String supplierId,
            int quantity,
            double unitCost) {

        RestockEntry entry = new RestockEntry(
                id,
                productId,
                supplierId,
                quantity,
                unitCost);

        requireProduct(productId);
        requireSupplier(supplierId);

        requireProduct(productId).increaseStock(quantity);
        repository.addRestock(entry);

        recordCashflow(
                "CF-RESTOCK-" + id,
                CashflowType.OUTFLOW,
                entry.getTotalCost(),
                "Restock " + id);

        return entry;
    }

    public CashflowEntry recordCashflow(
            String id,
            CashflowType type,
            double amount,
            String description) {

        CashflowEntry entry =
                new CashflowEntry(id, type, amount, description);

        repository.addCashflow(entry);

        return entry;
    }

    public double totalInflow() {
        double total = 0;

        for (CashflowEntry entry : repository.cashflows()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }

        return total;
    }

    public double totalOutflow() {
        double total = 0;

        for (CashflowEntry entry : repository.cashflows()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }

        return total;
    }

    public double netCashflow() {
        return totalInflow() - totalOutflow();
    }

    public void exportInventory(String filename) throws IOException {
        PrintWriter output = new PrintWriter(new FileWriter(filename));

        output.println("productId,name,category,stockQuantity,unitPrice,active");

        for (Product product : repository.products()) {
            output.println(
                    product.getId() + ","
                    + product.getName() + ","
                    + product.getCategory().getName() + ","
                    + product.getStockQuantity() + ","
                    + product.getUnitPrice() + ","
                    + product.isActive());
        }

        output.close();
        System.out.println("EXPORTED: " + filename);
    }

    public void exportCashflow(String filename) throws IOException {
        PrintWriter output = new PrintWriter(new FileWriter(filename));

        output.println("id,type,amount,description");

        for (CashflowEntry entry : repository.cashflows()) {
            output.println(
                    entry.getId() + ","
                    + entry.getType() + ","
                    + entry.getAmount() + ","
                    + entry.getDescription());
        }

        output.close();
        System.out.println("EXPORTED: " + filename);
    }

    public Collection<Product> products() {
        return repository.products();
    }

    public Collection<Order> orders() {
        return repository.orders();
    }

    public Collection<ReturnRequest> returns() {
        return repository.returns();
    }

    public Collection<RestockEntry> restocks() {
        return repository.restocks();
    }

    public Collection<CashflowEntry> cashflows() {
        return repository.cashflows();
    }

    public int productCount() {
        return repository.products().size();
    }

    public int customerCount() {
        return repository.customers().size();
    }

    public int supplierCount() {
        return repository.suppliers().size();
    }

    private Product requireProduct(String id) {
        Product product = repository.product(id);

        if (product == null) {
            throw new IllegalArgumentException("Unknown product: " + id);
        }

        return product;
    }

    private Customer requireCustomer(String id) {
        Customer customer = repository.customer(id);

        if (customer == null) {
            throw new IllegalArgumentException("Unknown customer: " + id);
        }

        return customer;
    }

    private Supplier requireSupplier(String id) {
        Supplier supplier = repository.supplier(id);

        if (supplier == null) {
            throw new IllegalArgumentException("Unknown supplier: " + id);
        }

        return supplier;
    }

    private Cart requireCart(String id) {
        Cart cart = repository.cart(id);

        if (cart == null) {
            throw new IllegalArgumentException("Unknown cart: " + id);
        }

        return cart;
    }

    private Order requireOrder(String id) {
        Order order = repository.order(id);

        if (order == null) {
            throw new IllegalArgumentException("Unknown order: " + id);
        }

        return order;
    }

    private ReturnRequest requireReturn(String id) {
        for (ReturnRequest returnRequest : repository.returns()) {
            if (returnRequest.getId().equals(id)) {
                return returnRequest;
            }
        }

        throw new IllegalArgumentException("Unknown return: " + id);
    }
}