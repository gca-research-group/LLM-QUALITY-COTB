package store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import store.model.CashflowEntry;
import store.model.CashflowType;
import store.model.Cart;
import store.model.CartItem;
import store.model.Category;
import store.model.Customer;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Payment;
import store.model.Product;
import store.model.Promotion;
import store.model.Restock;
import store.model.ReturnItem;
import store.model.ReturnRequest;
import store.model.ShippingMethod;
import store.model.ShippingRule;
import store.model.Supplier;
import store.repository.InMemoryRepository;

public class StoreService {
    private InMemoryRepository<Category> categories;
    private InMemoryRepository<Product> products;
    private InMemoryRepository<Customer> customers;
    private InMemoryRepository<Supplier> suppliers;
    private InMemoryRepository<Cart> carts;
    private InMemoryRepository<Order> orders;
    private InMemoryRepository<Payment> payments;
    private InMemoryRepository<ReturnRequest> returns;
    private InMemoryRepository<Restock> restocks;
    private InMemoryRepository<CashflowEntry> cashflow;
    private Map<String, Integer> stock;
    private List<Promotion> promotions;
    private Map<ShippingMethod, ShippingRule> shippingRules;

    public StoreService() {
        categories = new InMemoryRepository<Category>();
        products = new InMemoryRepository<Product>();
        customers = new InMemoryRepository<Customer>();
        suppliers = new InMemoryRepository<Supplier>();
        carts = new InMemoryRepository<Cart>();
        orders = new InMemoryRepository<Order>();
        payments = new InMemoryRepository<Payment>();
        returns = new InMemoryRepository<ReturnRequest>();
        restocks = new InMemoryRepository<Restock>();
        cashflow = new InMemoryRepository<CashflowEntry>();
        stock = new LinkedHashMap<String, Integer>();
        promotions = new ArrayList<Promotion>();
        shippingRules = new LinkedHashMap<ShippingMethod, ShippingRule>();
    }

    public void addCategory(Category category) {
        categories.save(category.getId(), category);
    }

    public void addProduct(Product product) {
        products.save(product.getId(), product);
    }

    public Product findProduct(String id) {
        return products.findById(id);
    }

    public List<Product> findProductsByName(String name) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : products.findAll()) {
            if (product.getName().toLowerCase().indexOf(name.toLowerCase()) >= 0) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> findProductsByCategory(String categoryId) {
        List<Product> result = new ArrayList<Product>();

        for (Product product : products.findAll()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }

        return result;
    }

    public void setProductActive(String id, boolean active) {
        Product product = products.findById(id);

        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        product.setActive(active);
    }

    public void increaseStock(String id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        requireProduct(id);
        stock.put(id, Integer.valueOf(getStock(id) + quantity));
    }

    public void decreaseStock(String id, int quantity) {
        if (quantity < 0 || getStock(id) < quantity) {
            throw new IllegalArgumentException("Insufficient stock or invalid quantity");
        }

        stock.put(id, Integer.valueOf(getStock(id) - quantity));
    }

    private void requireProduct(String id) {
        if (products.findById(id) == null) {
            throw new IllegalArgumentException("Product not found");
        }
    }

    private Product requireProductObject(String id) {
        requireProduct(id);
        return products.findById(id);
    }

    private Order requireOrder(String id) {
        Order order = orders.findById(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        return order;
    }

    public int getStock(String id) {
        Integer quantity = stock.get(id);

        if (quantity == null) {
            return 0;
        }

        return quantity.intValue();
    }

    public void addCustomer(Customer customer) {
        customers.save(customer.getId(), customer);
    }

    public void addSupplier(Supplier supplier) {
        suppliers.save(supplier.getId(), supplier);
    }

    public int productCount() {
        return products.findAll().size();
    }

    public int customerCount() {
        return customers.findAll().size();
    }

    public int supplierCount() {
        return suppliers.findAll().size();
    }

    public void addPromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    public void addShippingRule(ShippingRule rule) {
        shippingRules.put(rule.getMethod(), rule);
    }

    public void createCart(String id, String customerId) {
        Customer customer = customers.findById(customerId);

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        carts.save(id, new Cart(id, customer));
    }

    public void addToCart(String cartId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive");
        }

        Product product = requireProductObject(productId);

        if (!product.isActive()) {
            throw new IllegalArgumentException("Inactive product");
        }

        if (getStock(productId) < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        Cart cart = carts.findById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        CartItem existing = cart.findItem(productId);
        int requestedTotal = quantity;

        if (existing != null) {
            requestedTotal += existing.getQuantity();
        }

        if (getStock(productId) < requestedTotal) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        cart.add(product, quantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Cart quantity must be positive");
        }

        Cart cart = carts.findById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        requireProduct(productId);

        if (getStock(productId) < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        cart.update(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        Cart cart = carts.findById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        cart.remove(productId);
    }

    public double cartSubtotal(String id) {
        Cart cart = carts.findById(id);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        return cart.getSubtotal();
    }

    public Order createOrder(String orderId, String cartId) {
        Cart cart = carts.findById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        List<CartItem> cartItems = cart.getItems();

        if (cartItems.size() == 0) {
            throw new IllegalArgumentException("Empty cart");
        }

        List<OrderItem> items = new ArrayList<OrderItem>();

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);

            if (getStock(item.getProduct().getId()) < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock");
            }

            items.add(new OrderItem(
                item.getProduct(),
                item.getQuantity(),
                item.getProduct().getUnitPrice()
            ));
        }

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            decreaseStock(item.getProduct().getId(), item.getQuantity());
        }

        Order order = new Order(orderId, cart.getCustomer(), items);
        orders.save(orderId, order);
        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = requireOrder(orderId);
        double discount = promotion.calculateDiscount(order);

        if (discount > order.getSubtotal()) {
            discount = order.getSubtotal();
        }

        order.setPromotionDiscount(discount);
    }

    public void applyShipping(String orderId, ShippingMethod method) {
        Order order = requireOrder(orderId);
        ShippingRule rule = shippingRules.get(method);

        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule not found");
        }

        order.setShipping(rule.getCost());
    }

    public void pay(String paymentId, String orderId, double amount) {
        Order order = requireOrder(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled order cannot be paid");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Payment cannot be negative");
        }

        if (Math.abs(amount - order.getFinalTotal()) > 0.000001) {
            throw new IllegalArgumentException("Payment amount must equal final total");
        }

        payments.save(paymentId, new Payment(paymentId, orderId, amount));
        order.setStatus(OrderStatus.PAID);

        cashflow.save(
            paymentId,
            new CashflowEntry(
                paymentId,
                CashflowType.INFLOW,
                amount,
                "Payment for " + orderId
            )
        );
    }

    public void cancelOrder(String id) {
        Order order = requireOrder(id);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Paid order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        List<OrderItem> items = order.getItems();

        for (int i = 0; i < items.size(); i++) {
            increaseStock(items.get(i).getProduct().getId(), items.get(i).getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    public void createReturn(String id, String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        returns.save(id, new ReturnRequest(id, order));
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        ReturnRequest request = returns.findById(returnId);

        if (request == null) {
            throw new IllegalArgumentException("Return not found");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Return quantity must be positive");
        }

        Product product = requireProductObject(productId);

        int alreadyRequested = 0;
        List<ReturnItem> currentItems = request.getItems();

        for (int i = 0; i < currentItems.size(); i++) {
            if (currentItems.get(i).getProduct().getId().equals(productId)) {
                alreadyRequested += currentItems.get(i).getQuantity();
            }
        }

        if (alreadyRequested + quantity > request.getOrder().getSoldQuantity(productId)) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        request.addItem(new ReturnItem(product, quantity));
    }

    public double refundTotal(String id) {
        ReturnRequest request = returns.findById(id);

        if (request == null) {
            throw new IllegalArgumentException("Return not found");
        }

        return request.getRefundTotal();
    }

    public void processRefund(String id) {
        ReturnRequest request = returns.findById(id);

        if (request == null || request.isProcessed()) {
            throw new IllegalStateException("Invalid return");
        }

        double amount = refundTotal(id);
        List<ReturnItem> items = request.getItems();

        for (int i = 0; i < items.size(); i++) {
            increaseStock(
                items.get(i).getProduct().getId(),
                items.get(i).getQuantity()
            );
        }

        String cashflowId = "REF-" + id;

        cashflow.save(
            cashflowId,
            new CashflowEntry(
                cashflowId,
                CashflowType.OUTFLOW,
                amount,
                "Refund for " + id
            )
        );

        request.markProcessed();
        request.getOrder().setStatus(OrderStatus.RETURNED);
    }

    public void restock(String id, String productId, String supplierId, int quantity, double unitCost) {
        Product product = requireProductObject(productId);
        Supplier supplier = suppliers.findById(supplierId);

        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }

        Restock restock = new Restock(
            id,
            product,
            supplier,
            quantity,
            unitCost
        );

        increaseStock(productId, quantity);
        restocks.save(id, restock);

        String cashflowId = "REST-" + id;

        cashflow.save(
            cashflowId,
            new CashflowEntry(
                cashflowId,
                CashflowType.OUTFLOW,
                restock.getTotalCost(),
                "Restock " + id
            )
        );
    }

    public void manualOutflow(String id, double amount, String description) {
        cashflow.save(
            id,
            new CashflowEntry(
                id,
                CashflowType.OUTFLOW,
                amount,
                description
            )
        );
    }

    public List<Product> allProducts() {
        return products.findAll();
    }

    public Map<String, Integer> stockSnapshot() {
        return new LinkedHashMap<String, Integer>(stock);
    }

    public List<Order> allOrders() {
        return orders.findAll();
    }

    public List<ReturnRequest> allReturns() {
        return returns.findAll();
    }

    public List<Restock> allRestocks() {
        return restocks.findAll();
    }

    public List<CashflowEntry> allCashflow() {
        return cashflow.findAll();
    }

    public double totalInflow() {
        double total = 0;
        List<CashflowEntry> entries = cashflow.findAll();

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getType() == CashflowType.INFLOW) {
                total += entries.get(i).getAmount();
            }
        }

        return total;
    }

    public double totalOutflow() {
        double total = 0;
        List<CashflowEntry> entries = cashflow.findAll();

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getType() == CashflowType.OUTFLOW) {
                total += entries.get(i).getAmount();
            }
        }

        return total;
    }

    public int orderCount(OrderStatus status) {
        int count = 0;

        for (Order order : orders.findAll()) {
            if (order.getStatus() == status) {
                count++;
            }
        }

        return count;
    }

    public int soldQuantity(String productId) {
        int quantity = 0;

        for (Order order : orders.findAll()) {
            if (order.getStatus() != OrderStatus.CANCELLED) {
                List<OrderItem> items = order.getItems();

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getProduct().getId().equals(productId)) {
                        quantity += items.get(i).getQuantity();
                    }
                }
            }
        }

        return quantity;
    }
}