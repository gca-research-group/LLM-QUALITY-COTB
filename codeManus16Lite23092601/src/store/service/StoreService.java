package store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import store.model.Models.Cart;
import store.model.Models.CartItem;
import store.model.Models.Category;
import store.model.Models.Customer;
import store.model.Models.LoyaltyTier;
import store.model.Models.OrderStatus;
import store.model.Models.Product;
import store.model.Models.Supplier;
import store.model.Models.CashflowType;
import store.model.Models.ShippingType;
import store.model.Transactions.CashflowEntry;
import store.model.Transactions.FixedPromotion;
import store.model.Transactions.Order;
import store.model.Transactions.OrderItem;
import store.model.Transactions.Payment;
import store.model.Transactions.Promotion;
import store.model.Transactions.Restock;
import store.model.Transactions.ReturnItem;
import store.model.Transactions.ReturnRequest;
import store.repository.DataStore;

public final class StoreService {
    private final DataStore db;
    public StoreService(DataStore db) { this.db = db; }
    public void addCategory(Category c) { unique(db.categories, c.getId(), "Category"); db.categories.put(c.getId(), c); }
    public void addProduct(Product p) { unique(db.products, p.getId(), "Product"); db.products.put(p.getId(), p); }
    public Product product(String id) { return get(db.products, id, "Product"); }
    public List<Product> searchByName(String name) { return db.products.values().stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).toList(); }
    public List<Product> byCategory(String categoryId) { return db.products.values().stream().filter(p -> p.getCategory().getId().equals(categoryId)).toList(); }
    public void activate(String id) { product(id).setActive(true); }
    public void deactivate(String id) { product(id).setActive(false); }
    public void increaseStock(String id, int quantity) { product(id).increaseStock(quantity); }
    public void decreaseStock(String id, int quantity) { product(id).decreaseStock(quantity); }
    public void registerPromotion(Promotion promotion) { if (promotion == null) throw new IllegalArgumentException("Promotion is required"); db.promotions.add(promotion); }
    public void addCustomer(Customer c) { unique(db.customers, c.getId(), "Customer"); db.customers.put(c.getId(), c); }
    public Customer customer(String id) { return get(db.customers, id, "Customer"); }
    public void addSupplier(Supplier s) { unique(db.suppliers, s.getId(), "Supplier"); db.suppliers.put(s.getId(), s); }
    public Supplier supplier(String id) { return get(db.suppliers, id, "Supplier"); }
    public Cart createCart(String id, String customerId) { unique(db.carts, id, "Cart"); Cart c = new Cart(id, customer(customerId)); db.carts.put(id, c); return c; }
    public void addToCart(String cartId, String productId, int quantity) { Product p = product(productId); if (!p.isActive()) throw new IllegalArgumentException("Inactive product"); cart(cartId).add(p, quantity); }
    public void updateCart(String cartId, String productId, int quantity) { cart(cartId).update(productId, quantity); }
    public void removeFromCart(String cartId, String productId) { cart(cartId).remove(productId); }
    public double cartSubtotal(String id) { return cart(id).subtotal(); }
    public Order createOrder(String orderId, String cartId) { unique(db.orders, orderId, "Order"); Cart c = cart(cartId); if (c.getItems().isEmpty()) throw new IllegalArgumentException("Cart is empty"); List<OrderItem> items = new ArrayList<>(); for (CartItem i : c.getItems()) { i.getProduct().decreaseStock(i.getQuantity()); items.add(new OrderItem(i.getProduct(), i.getQuantity(), i.getProduct().getUnitPrice())); } Order o = new Order(orderId, c.getCustomer(), items); db.orders.put(orderId, o); return o; }
    public void applyPromotion(String orderId, Promotion promotion) { Order o = order(orderId); o.setPromotionDiscount(promotion.discount(o)); }
    public void applyShipping(String orderId, ShippingType type) { double value = switch (type) { case STANDARD -> 12.0; case EXPRESS -> 25.0; case PICKUP -> 0.0; }; order(orderId).setShipping(value); }
    public void pay(String orderId, Payment payment) { Order o = order(orderId); if (o.getStatus() == OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled order cannot be paid"); if (Math.abs(payment.getAmount() - o.total()) > 0.001) throw new IllegalArgumentException("Payment does not match total"); o.setStatus(OrderStatus.PAID); db.cashflow.add(new CashflowEntry(payment.getId(), CashflowType.INFLOW, payment.getAmount(), "Payment for " + orderId)); }
    public void cancel(String orderId) { Order o = order(orderId); if (o.getStatus() == OrderStatus.PAID) throw new IllegalStateException("Paid order cannot be cancelled"); if (o.getStatus() == OrderStatus.CANCELLED) return; for (OrderItem i : o.getItems()) i.getProduct().increaseStock(i.getQuantity()); o.setStatus(OrderStatus.CANCELLED); }
    public ReturnRequest createReturn(String id, String orderId) { unique(db.returns, id, "Return"); Order o = order(orderId); if (o.getStatus() != OrderStatus.PAID && o.getStatus() != OrderStatus.PARTIALLY_RETURNED) throw new IllegalStateException("Only paid orders can be returned"); ReturnRequest r = new ReturnRequest(id, o); db.returns.put(id, r); return r; }
    public void addReturnItem(String returnId, String productId, int quantity) { ReturnRequest r = returnRequest(returnId); OrderItem bought = r.getOrder().getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Product not bought")); int already = r.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToInt(ReturnItem::getQuantity).sum(); if (already + quantity > bought.getQuantity()) throw new IllegalArgumentException("Returned quantity exceeds bought quantity"); r.add(new ReturnItem(bought.getProduct(), quantity)); }
    public double refundTotal(String returnId) { ReturnRequest r = returnRequest(returnId); return r.getItems().stream().mapToDouble(i -> i.getProduct().getUnitPrice() * i.getQuantity()).sum(); }
    public void processReturn(String returnId) { ReturnRequest r = returnRequest(returnId); if (r.getItems().isEmpty()) throw new IllegalArgumentException("Return needs items"); double total = refundTotal(returnId); for (ReturnItem i : r.getItems()) i.getProduct().increaseStock(i.getQuantity()); r.process(total); r.getOrder().setStatus(OrderStatus.RETURNED); db.cashflow.add(new CashflowEntry("REF-" + returnId, CashflowType.OUTFLOW, total, "Refund for " + r.getOrder().getId())); }
    public Restock restock(String id, String productId, String supplierId, int quantity, double unitCost) { unique(db.restocks, id, "Restock"); Product p = product(productId); Restock r = new Restock(id, p, supplier(supplierId), quantity, unitCost); p.increaseStock(quantity); db.restocks.put(id, r); db.cashflow.add(new CashflowEntry("RESTOCK-" + id, CashflowType.OUTFLOW, r.getTotalCost(), "Restock " + id)); return r; }
    public void manualOutflow(String id, double amount, String description) { db.cashflow.add(new CashflowEntry(id, CashflowType.OUTFLOW, amount, description)); }
    public DataStore data() { return db; }
    private Cart cart(String id) { return get(db.carts, id, "Cart"); } private Order order(String id) { return get(db.orders, id, "Order"); } private ReturnRequest returnRequest(String id) { return get(db.returns, id, "Return"); }
    private static <T> T get(Map<String,T> map, String id, String type) { T value = map.get(id); if (value == null) throw new IllegalArgumentException(type + " not found: " + id); return value; }
    private static <T> void unique(Map<String,T> map, String id, String type) { if (map.containsKey(id)) throw new IllegalArgumentException(type + " identifier already exists: " + id); }
}
