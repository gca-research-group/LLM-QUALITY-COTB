package store.service;

import store.domain.Domain;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StoreService {
    private final Map<String, Domain.Category> categories = new LinkedHashMap<>();
    private final Map<String, Domain.Product> products = new LinkedHashMap<>();
    private final Map<String, Domain.Customer> customers = new LinkedHashMap<>();
    private final Map<String, Domain.Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Domain.Cart> carts = new LinkedHashMap<>();
    private final Map<String, Domain.Order> orders = new LinkedHashMap<>();
    private final Map<String, Domain.ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, Domain.Restock> restocks = new LinkedHashMap<>();
    private final List<Domain.CashflowEntry> cashflow = new ArrayList<>();
    private final List<Domain.Promotion> promotions = new ArrayList<>();
    private final Map<Domain.ShippingMethod, Double> shipping = new EnumMap<>(Domain.ShippingMethod.class);

    public void addCategory(Domain.Category c) { unique(categories, c.getId(), c, "category"); }
    public void addProduct(Domain.Product p) { unique(products, p.getId(), p, "product"); }
    public void addCustomer(Domain.Customer c) { unique(customers, c.getId(), c, "customer"); }
    public void addSupplier(Domain.Supplier s) { unique(suppliers, s.getId(), s, "supplier"); }
    private <T> void unique(Map<String,T> map, String id, T value, String type) { if (map.containsKey(id)) throw new IllegalArgumentException("Duplicate " + type + " identifier"); map.put(id, value); }
    public Domain.Product product(String id) { return require(products, id, "product"); }
    public Domain.Customer customer(String id) { return require(customers, id, "customer"); }
    public Domain.Supplier supplier(String id) { return require(suppliers, id, "supplier"); }
    public Domain.Order order(String id) { return require(orders, id, "order"); }
    public Map<String, Domain.Product> products() { return products; } public Map<String, Domain.Customer> customers() { return customers; } public Map<String, Domain.Supplier> suppliers() { return suppliers; } public Map<String, Domain.Order> orders() { return orders; } public Map<String, Domain.ReturnRequest> returns() { return returns; } public Map<String, Domain.Restock> restocks() { return restocks; } public List<Domain.CashflowEntry> cashflow() { return cashflow; }
    private <T> T require(Map<String,T> map, String id, String type) { T value = map.get(id); if (value == null) throw new IllegalArgumentException("Unknown " + type + ": " + id); return value; }
    public List<Domain.Product> searchByName(String text) { List<Domain.Product> result = new ArrayList<>(); for (Domain.Product p : products.values()) if (p.getName().toLowerCase().contains(text.toLowerCase())) result.add(p); return result; }
    public List<Domain.Product> byCategory(String categoryId) { List<Domain.Product> result = new ArrayList<>(); for (Domain.Product p : products.values()) if (p.getCategory().getId().equals(categoryId)) result.add(p); return result; }
    public void increaseStock(String productId, int quantity) { if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative"); product(productId).increaseStock(quantity); }
    public void decreaseStock(String productId, int quantity) { product(productId).decreaseStock(quantity); }
    public Domain.Cart createCart(String id, String customerId) { Domain.Cart c = new Domain.Cart(id, customer(customerId)); carts.put(id, c); return c; }
    public Domain.Cart cart(String id) { return require(carts, id, "cart"); }
    public void addToCart(String cartId, String productId, int quantity) { cart(cartId).add(product(productId), quantity); }
    public void updateCart(String cartId, String productId, int quantity) { cart(cartId).update(productId, quantity); }
    public void removeFromCart(String cartId, String productId) { cart(cartId).remove(productId); }
    public void addPromotion(Domain.Promotion promotion) { promotions.add(promotion); }
    public void setShipping(Domain.ShippingMethod method, double cost) { if (cost < 0) throw new IllegalArgumentException("Shipping cannot be negative"); shipping.put(method, cost); }
    public Domain.Order createOrder(String id, String cartId) { Domain.Cart cart = cart(cartId); if (cart.getItems().isEmpty()) throw new IllegalArgumentException("Cart is empty"); List<Domain.OrderItem> items = new ArrayList<>(); for (Domain.CartItem ci : cart.getItems().values()) { decreaseStock(ci.getProduct().getId(), ci.getQuantity()); items.add(new Domain.OrderItem(ci.getProduct(), ci.getQuantity(), ci.getProduct().getUnitPrice())); } Domain.Order order = new Domain.Order(id, cart.getCustomer(), items); orders.put(id, order); return order; }
    public void applyPromotion(String orderId, Domain.Promotion promotion) { Domain.Order o = order(orderId); o.setPromotionDiscount(promotion.discount(o)); }
    public void applyShipping(String orderId, Domain.ShippingMethod method) { order(orderId).setShippingCost(shipping.getOrDefault(method, 0.0)); }
    public void pay(String orderId, Domain.PaymentMethod method) { Domain.Order o = order(orderId); if (o.getStatus() == Domain.OrderStatus.CANCELLED) throw new IllegalStateException("Cancelled order cannot be paid"); if (o.getStatus() != Domain.OrderStatus.CREATED) throw new IllegalStateException("Order is not payable"); o.setStatus(Domain.OrderStatus.PAID); cashflow.add(new Domain.CashflowEntry(Domain.CashflowType.INFLOW, "Payment " + orderId + " " + method, o.total())); }
    public void cancel(String orderId) { Domain.Order o = order(orderId); if (o.getStatus() == Domain.OrderStatus.PAID) throw new IllegalStateException("Paid order cannot be cancelled"); if (o.getStatus() == Domain.OrderStatus.CANCELLED) return; for (Domain.OrderItem i : o.getItems()) increaseStock(i.getProduct().getId(), i.getQuantity()); o.setStatus(Domain.OrderStatus.CANCELLED); }
    public Domain.ReturnRequest createReturn(String id, String orderId) { Domain.Order o = order(orderId); if (o.getStatus() != Domain.OrderStatus.PAID) throw new IllegalStateException("Only paid orders can be returned"); Domain.ReturnRequest r = new Domain.ReturnRequest(id, o); returns.put(id, r); return r; }
    public void addReturnItem(String returnId, String productId, int quantity) { Domain.ReturnRequest r = require(returns, returnId, "return"); int bought = r.getOrder().getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToInt(Domain.OrderItem::getQuantity).findFirst().orElse(0); int already = r.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToInt(Domain.ReturnItem::getQuantity).sum(); if (quantity <= 0 || already + quantity > bought) throw new IllegalArgumentException("Returned quantity exceeds purchase"); r.add(new Domain.ReturnItem(product(productId), quantity)); }
    public void processRefund(String returnId) { Domain.ReturnRequest r = require(returns, returnId, "return"); if (r.getItems().isEmpty()) throw new IllegalArgumentException("Return needs items"); if (r.isProcessed()) throw new IllegalStateException("Return already processed"); for (Domain.ReturnItem i : r.getItems()) increaseStock(i.getProduct().getId(), i.getQuantity()); double total = r.total(); r.markProcessed(); r.getOrder().setStatus(Domain.OrderStatus.REFUNDED); cashflow.add(new Domain.CashflowEntry(Domain.CashflowType.OUTFLOW, "Refund " + returnId, total)); }
    public Domain.Restock restock(String id, String productId, String supplierId, int quantity, double unitCost) { Domain.Restock r = new Domain.Restock(id, product(productId), supplier(supplierId), quantity, unitCost); increaseStock(productId, quantity); restocks.put(id, r); cashflow.add(new Domain.CashflowEntry(Domain.CashflowType.OUTFLOW, "Restock " + id, r.totalCost())); return r; }
    public void manualOutflow(String description, double amount) { cashflow.add(new Domain.CashflowEntry(Domain.CashflowType.OUTFLOW, description, amount)); }
    public double totalInflow() { return cashflow.stream().filter(e -> e.getType() == Domain.CashflowType.INFLOW).mapToDouble(Domain.CashflowEntry::getAmount).sum(); }
    public double totalOutflow() { return cashflow.stream().filter(e -> e.getType() == Domain.CashflowType.OUTFLOW).mapToDouble(Domain.CashflowEntry::getAmount).sum(); }
}
