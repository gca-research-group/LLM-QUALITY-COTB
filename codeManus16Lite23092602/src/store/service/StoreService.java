package store.service;

import java.util.*;
import store.model.StoreTypes;
import store.model.StoreTypes.*;
import store.model.Rules;

public final class StoreService {
    private final Map<String, Category> categories = new LinkedHashMap<>();
    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Cart> carts = new LinkedHashMap<>();
    private final Map<String, Order> orders = new LinkedHashMap<>();
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, RestockEntry> restocks = new LinkedHashMap<>();
    private final List<CashflowEntry> cashflow = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<Rules.Promotion> promotions = new ArrayList<>();
    private int cashflowSequence;

    public void addCategory(Category category) { unique(categories, category.getId(), "category"); categories.put(category.getId(), category); }
    public void addProduct(Product product) { if (!categories.containsKey(product.getCategory().getId())) throw new IllegalArgumentException("category not registered"); unique(products, product.getId(), "product"); products.put(product.getId(), product); }
    public Product product(String id) { return require(products, id, "product"); }
    public void setStock(String id, int quantity) { if (quantity < 0) throw new IllegalArgumentException("stock cannot be negative"); Product p = product(id); if (p.getStock() > 0) p.decreaseStock(p.getStock()); if (quantity > 0) p.increaseStock(quantity); }
    public void increaseStock(String id, int quantity) { product(id).increaseStock(quantity); }
    public void decreaseStock(String id, int quantity) { product(id).decreaseStock(quantity); }
    public void activate(String id) { product(id).setActive(true); }
    public void deactivate(String id) { product(id).setActive(false); }
    public List<Product> searchByName(String text) { return products.values().stream().filter(p -> p.getName().toLowerCase().contains(text.toLowerCase())).toList(); }
    public List<Product> byCategory(String categoryId) { return products.values().stream().filter(p -> p.getCategory().getId().equals(categoryId)).toList(); }
    public void registerCustomer(Customer customer) { unique(customers, customer.getId(), "customer"); customers.put(customer.getId(), customer); }
    public void registerSupplier(Supplier supplier) { unique(suppliers, supplier.getId(), "supplier"); suppliers.put(supplier.getId(), supplier); }
    public Customer customer(String id) { return require(customers, id, "customer"); }
    public Supplier supplier(String id) { return require(suppliers, id, "supplier"); }
    public void addPromotion(Rules.Promotion promotion) { promotions.add(Objects.requireNonNull(promotion)); }
    public Cart createCart(String id, String customerId) { if (carts.containsKey(id)) throw new IllegalArgumentException("duplicate cart"); Cart c = new Cart(id, customer(customerId)); carts.put(id, c); return c; }
    public Cart cart(String id) { return require(carts, id, "cart"); }
    public Order createOrder(String id, String cartId) { if (orders.containsKey(id)) throw new IllegalArgumentException("duplicate order"); Cart cart = cart(cartId); if (cart.isEmpty()) throw new IllegalArgumentException("order must contain items"); List<OrderItem> items = cart.getItems().stream().map(i -> new OrderItem(i.getProduct(), i.getQuantity(), i.getProduct().getUnitPrice())).toList(); items.forEach(i -> decreaseStock(i.getProduct().getId(), i.getQuantity())); Order order = new Order(id, cart.getCustomer(), items); orders.put(id, order); return order; }
    public Order order(String id) { return require(orders, id, "order"); }
    public void applyPromotion(String orderId, Rules.Promotion promotion) { Order order = order(orderId); order.setPromotionDiscount(Math.min(order.subtotal(), Math.max(0, promotion.discount(order)))); }
    public void applyFirstEligiblePromotion(String orderId) { Order order = order(orderId); double best = promotions.stream().mapToDouble(p -> p.discount(order)).max().orElse(0); order.setPromotionDiscount(Math.min(order.subtotal(), Math.max(0, best))); }
    public void applyShipping(String orderId, Rules.ShippingRule rule) { order(orderId).setShipping(rule.cost(order(orderId))); }
    public Payment pay(String paymentId, String orderId, PaymentMethod method) { Order order = order(orderId); if (order.getStatus() == OrderStatus.CANCELLED) throw new IllegalStateException("cancelled order cannot be paid"); if (order.getStatus() == OrderStatus.PAID) throw new IllegalStateException("order already paid"); order.markPaid(); Payment payment = new Payment(paymentId, orderId, order.total(), method); payments.add(payment); addCashflow(CashflowType.INFLOW, payment.getAmount(), "Payment " + paymentId); return payment; }
    public void cancel(String orderId) { Order order = order(orderId); order.cancel(); order.getItems().forEach(i -> increaseStock(i.getProduct().getId(), i.getQuantity())); }
    public ReturnRequest createReturn(String id, String orderId) { if (returns.containsKey(id)) throw new IllegalArgumentException("duplicate return"); ReturnRequest request = new ReturnRequest(id, order(orderId)); returns.put(id, request); return request; }
    public void processReturn(String returnId) { ReturnRequest r = require(returns, returnId, "return"); if (r.isProcessed()) throw new IllegalStateException("return already processed"); r.calculate(); r.getQuantities().forEach((productId, quantity) -> increaseStock(productId, quantity)); addCashflow(CashflowType.OUTFLOW, r.getRefundTotal(), "Refund " + returnId); r.getOrder().markRefunded(); r.markProcessed(); }
    public RestockEntry restock(String id, String productId, String supplierId, int quantity, double unitCost) { if (restocks.containsKey(id)) throw new IllegalArgumentException("duplicate restock"); supplier(supplierId); RestockEntry entry = new RestockEntry(id, productId, supplierId, quantity, unitCost); increaseStock(productId, quantity); restocks.put(id, entry); addCashflow(CashflowType.OUTFLOW, entry.totalCost(), "Restock " + id); return entry; }
    public void manualOutflow(double amount, String description) { addCashflow(CashflowType.OUTFLOW, amount, description); }
    private void addCashflow(CashflowType type, double amount, String description) { cashflow.add(new CashflowEntry("CF" + (++cashflowSequence), type, amount, description)); }
    public Collection<Product> products() { return Collections.unmodifiableCollection(products.values()); }
    public Collection<Customer> customers() { return Collections.unmodifiableCollection(customers.values()); }
    public Collection<Supplier> suppliers() { return Collections.unmodifiableCollection(suppliers.values()); }
    public Collection<Order> orders() { return Collections.unmodifiableCollection(orders.values()); }
    public Collection<ReturnRequest> returns() { return Collections.unmodifiableCollection(returns.values()); }
    public Collection<RestockEntry> restocks() { return Collections.unmodifiableCollection(restocks.values()); }
    public List<CashflowEntry> cashflow() { return Collections.unmodifiableList(cashflow); }
    public Map<String,Integer> salesByProduct() { Map<String,Integer> result = new TreeMap<>(); orders.values().forEach(o -> o.getItems().forEach(i -> result.merge(i.getProduct().getId(), i.getQuantity(), Integer::sum))); return result; }
    public Map<OrderStatus,Long> ordersByStatus() { Map<OrderStatus,Long> result = new EnumMap<>(OrderStatus.class); for (OrderStatus s : OrderStatus.values()) result.put(s, orders.values().stream().filter(o -> o.getStatus() == s).count()); return result; }
    public double totalInflow() { return cashflow.stream().filter(e -> e.getType() == CashflowType.INFLOW).mapToDouble(CashflowEntry::getAmount).sum(); }
    public double totalOutflow() { return cashflow.stream().filter(e -> e.getType() == CashflowType.OUTFLOW).mapToDouble(CashflowEntry::getAmount).sum(); }
    private static <T> void unique(Map<String,T> map, String id, String kind) { if (map.containsKey(id)) throw new IllegalArgumentException("duplicate " + kind + " identifier"); map.put(id, null); }
    private static <T> T require(Map<String,T> map, String id, String kind) { T value = map.get(id); if (value == null) throw new IllegalArgumentException(kind + " not found: " + id); return value; }
}
