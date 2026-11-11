package store.service;

import store.model.Model;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StoreService {
    private final Map<String, Model.Category> categories = new LinkedHashMap<>();
    private final Map<String, Model.Product> products = new LinkedHashMap<>();
    private final Map<String, Model.Customer> customers = new LinkedHashMap<>();
    private final Map<String, Model.Supplier> suppliers = new LinkedHashMap<>();
    private final Map<String, Model.Cart> carts = new LinkedHashMap<>();
    private final Map<String, Model.Order> orders = new LinkedHashMap<>();
    private final Map<String, Model.ReturnRequest> returns = new LinkedHashMap<>();
    private final Map<String, Model.Restock> restocks = new LinkedHashMap<>();
    private final List<Model.CashflowEntry> cashflow = new ArrayList<>();
    private int cashflowSequence;

    public void addCategory(Model.Category category) { requireNew(categories, category.getId(), "category"); categories.put(category.getId(), category); }
    public void addProduct(Model.Product product) { requireNew(products, product.getId(), "product"); products.put(product.getId(), product); }
    public void registerCustomer(Model.Customer customer) { requireNew(customers, customer.getId(), "customer"); customers.put(customer.getId(), customer); }
    public void registerSupplier(Model.Supplier supplier) { requireNew(suppliers, supplier.getId(), "supplier"); suppliers.put(supplier.getId(), supplier); }
    private <T> void requireNew(Map<String, T> map, String id, String type) { if (map.containsKey(id)) throw new IllegalArgumentException("duplicate " + type + " id: " + id); }
    public Model.Product findProduct(String id) { return require(products, id, "product"); }
    public Model.Customer findCustomer(String id) { return require(customers, id, "customer"); }
    public Model.Supplier findSupplier(String id) { return require(suppliers, id, "supplier"); }
    private <T> T require(Map<String, T> map, String id, String type) { T value = map.get(id); if (value == null) throw new IllegalArgumentException(type + " not found: " + id); return value; }
    public List<Model.Product> searchByName(String name) { return products.values().stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).toList(); }
    public List<Model.Product> listByCategory(String categoryId) { return products.values().stream().filter(p -> p.getCategory().getId().equals(categoryId)).toList(); }
    public void setProductActive(String id, boolean active) { findProduct(id).setActive(active); }
    public void increaseStock(String id, int quantity) { findProduct(id).increaseStock(quantity); }
    public void decreaseStock(String id, int quantity) { findProduct(id).decreaseStock(quantity); }
    public Model.Cart createCart(String id, String customerId) { requireNew(carts, id, "cart"); Model.Cart cart = new Model.Cart(id, findCustomer(customerId)); carts.put(id, cart); return cart; }
    public void addToCart(String cartId, String productId, int quantity) { Model.Product product = findProduct(productId); if (!product.isActive()) throw new IllegalArgumentException("inactive product cannot be added"); require(carts, cartId, "cart").put(productId, quantity); }
    public void updateCartQuantity(String cartId, String productId, int quantity) { if (!require(carts, cartId, "cart").getItems().containsKey(productId)) throw new IllegalArgumentException("product is not in cart"); addToCart(cartId, productId, quantity); }
    public void removeFromCart(String cartId, String productId) { require(carts, cartId, "cart").remove(productId); }
    public double cartSubtotal(String cartId) { Model.Cart cart = require(carts, cartId, "cart"); return cart.getItems().entrySet().stream().mapToDouble(e -> findProduct(e.getKey()).getUnitPrice() * e.getValue()).sum(); }
    public Model.Order createOrder(String id, String cartId) { requireNew(orders, id, "order"); Model.Cart cart = require(carts, cartId, "cart"); if (cart.isEmpty()) throw new IllegalArgumentException("order must contain an item"); List<Model.OrderItem> items = new ArrayList<>(); for (Map.Entry<String, Integer> e : cart.getItems().entrySet()) { Model.Product p = findProduct(e.getKey()); p.decreaseStock(e.getValue()); items.add(new Model.OrderItem(p, e.getValue(), p.getUnitPrice())); } Model.Order order = new Model.Order(id, cart.getCustomer(), items); orders.put(id, order); return order; }
    public void applyPercentagePromotion(String orderId, double threshold, double percentage) { Model.Order o = order(orderId); if (o.subtotal() >= threshold) o.setPromotionDiscount(o.subtotal() * percentage / 100.0); }
    public void applyGoldFixedPromotion(String orderId, double threshold, double discount) { Model.Order o = order(orderId); if (o.getCustomer().getLoyaltyTier() == Model.LoyaltyTier.GOLD && o.subtotal() >= threshold) o.setPromotionDiscount(discount); }
    public void applyBuyXGetY(String orderId, String productId, int buy, int free) { if (buy <= 0 || free <= 0) throw new IllegalArgumentException("promotion quantities must be positive"); Model.Order o = order(orderId); for (Model.OrderItem item : o.getItems()) if (item.getProduct().getId().equals(productId)) { int groups = item.getQuantity() / (buy + free); o.setPromotionDiscount(Math.max(o.getPromotionDiscount(), groups * free * item.getUnitPrice())); } }
    public void applyShipping(String orderId, Model.ShippingType type) { Model.Order o = order(orderId); double cost = type == Model.ShippingType.PICKUP ? 0 : type == Model.ShippingType.STANDARD ? 12 : 25; o.setShippingCost(cost); }
    public Model.Order order(String id) { return require(orders, id, "order"); }
    public void pay(String orderId, double amount, Model.PaymentMethod method) { Model.Order o = order(orderId); if (o.getStatus() == Model.OrderStatus.CANCELLED) throw new IllegalArgumentException("cancelled order cannot be paid"); if (amount < o.total()) throw new IllegalArgumentException("payment is insufficient"); if (method == null) throw new IllegalArgumentException("payment method is required"); o.setStatus(Model.OrderStatus.PAID); addCashflow(Model.CashflowType.INFLOW, amount, "payment for " + orderId); }
    public void cancel(String orderId) { Model.Order o = order(orderId); if (o.getStatus() == Model.OrderStatus.PAID) throw new IllegalArgumentException("paid order cannot be cancelled"); if (o.getStatus() == Model.OrderStatus.CANCELLED) throw new IllegalArgumentException("order already cancelled"); for (Model.OrderItem item : o.getItems()) item.getProduct().increaseStock(item.getQuantity()); o.setStatus(Model.OrderStatus.CANCELLED); }
    public Model.ReturnRequest createReturn(String id, String orderId) { requireNew(returns, id, "return"); Model.Order o = order(orderId); if (o.getStatus() != Model.OrderStatus.PAID && o.getStatus() != Model.OrderStatus.PARTIALLY_RETURNED) throw new IllegalArgumentException("only paid orders can be returned"); Model.ReturnRequest request = new Model.ReturnRequest(id, o); returns.put(id, request); return request; }
    public void addReturnedItem(String returnId, String productId, int quantity) { Model.ReturnRequest r = require(returns, returnId, "return"); int bought = r.getOrder().getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToInt(Model.OrderItem::getQuantity).sum(); int already = r.getItems().getOrDefault(productId, 0); if (already + quantity > bought) throw new IllegalArgumentException("returned quantity exceeds bought quantity"); r.add(productId, quantity); }
    public double calculateRefund(String returnId) { Model.ReturnRequest r = require(returns, returnId, "return"); double total = 0; for (Map.Entry<String, Integer> e : r.getItems().entrySet()) for (Model.OrderItem item : r.getOrder().getItems()) if (item.getProduct().getId().equals(e.getKey())) total += e.getValue() * item.getUnitPrice(); r.setRefundTotal(total); return total; }
    public void processRefund(String returnId) { Model.ReturnRequest r = require(returns, returnId, "return"); if (r.getItems().isEmpty()) throw new IllegalArgumentException("return must contain an item"); if (r.isProcessed()) throw new IllegalArgumentException("return already processed"); calculateRefund(returnId); for (Map.Entry<String, Integer> e : r.getItems().entrySet()) findProduct(e.getKey()).increaseStock(e.getValue()); addCashflow(Model.CashflowType.OUTFLOW, r.getRefundTotal(), "refund for " + returnId); r.setProcessed(true); r.getOrder().setStatus(Model.OrderStatus.RETURNED); }
    public Model.Restock restock(String id, String productId, String supplierId, int quantity, double unitCost) { requireNew(restocks, id, "restock"); if (quantity <= 0) throw new IllegalArgumentException("restock quantity must be positive"); Model.Restock restock = new Model.Restock(id, findProduct(productId), findSupplier(supplierId), quantity, unitCost); restocks.put(id, restock); restock.getProduct().increaseStock(quantity); addCashflow(Model.CashflowType.OUTFLOW, restock.totalCost(), "restock " + id); return restock; }
    public void addManualOutflow(String description, double amount) { addCashflow(Model.CashflowType.OUTFLOW, amount, description); }
    private void addCashflow(Model.CashflowType type, double amount, String description) { cashflow.add(new Model.CashflowEntry("CF" + (++cashflowSequence), type, amount, description)); }
    public Map<Model.OrderStatus, Long> ordersByStatus() { Map<Model.OrderStatus, Long> result = new EnumMap<>(Model.OrderStatus.class); for (Model.OrderStatus status : Model.OrderStatus.values()) result.put(status, orders.values().stream().filter(o -> o.getStatus() == status).count()); return result; }
    public Map<String, Integer> salesByProduct() { Map<String, Integer> result = new LinkedHashMap<>(); for (Model.Product p : products.values()) result.put(p.getId(), 0); for (Model.Order o : orders.values()) if (o.getStatus() != Model.OrderStatus.CANCELLED) for (Model.OrderItem i : o.getItems()) result.merge(i.getProduct().getId(), i.getQuantity(), Integer::sum); return result; }
    public double totalInflow() { return cashflow.stream().filter(e -> e.getType() == Model.CashflowType.INFLOW).mapToDouble(Model.CashflowEntry::getAmount).sum(); }
    public double totalOutflow() { return cashflow.stream().filter(e -> e.getType() == Model.CashflowType.OUTFLOW).mapToDouble(Model.CashflowEntry::getAmount).sum(); }
    public List<Model.Product> products() { return new ArrayList<>(products.values()); }
    public List<Model.Order> orders() { return new ArrayList<>(orders.values()); }
    public List<Model.ReturnRequest> returns() { return new ArrayList<>(returns.values()); }
    public List<Model.Restock> restocks() { return new ArrayList<>(restocks.values()); }
    public List<Model.CashflowEntry> cashflow() { return new ArrayList<>(cashflow); }
    public void exportInventory(String filename) throws IOException { try (BufferedWriter w = Files.newBufferedWriter(Path.of(filename))) { w.write("productId,name,category,stock,unitPrice"); w.newLine(); for (Model.Product p : products.values()) { w.write(String.join(",", p.getId(), p.getName(), p.getCategory().getName(), Integer.toString(p.getStock()), Double.toString(p.getUnitPrice()))); w.newLine(); } } }
    public void exportCashflow(String filename) throws IOException { try (BufferedWriter w = Files.newBufferedWriter(Path.of(filename))) { w.write("id,type,amount,description"); w.newLine(); for (Model.CashflowEntry e : cashflow) { w.write(String.join(",", e.getId(), e.getType().name(), Double.toString(e.getAmount()), e.getDescription().replace(",", " "))); w.newLine(); } } }
    public int productCount() { return products.size(); }
    public int customerCount() { return customers.size(); }
    public int supplierCount() { return suppliers.size(); }
}
