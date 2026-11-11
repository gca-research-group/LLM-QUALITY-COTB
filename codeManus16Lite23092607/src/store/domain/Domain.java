package store.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Domain {
    private Domain() {}

    public enum LoyaltyTier { REGULAR, SILVER, GOLD }
    public enum OrderStatus { CREATED, PAID, CANCELLED, REFUNDED }
    public enum PaymentMethod { CARD, PIX, CASH }
    public enum CashflowType { INFLOW, OUTFLOW }
    public enum ShippingMethod { STANDARD, EXPRESS, PICKUP }

    public static final class Category {
        private final String id;
        private final String name;
        public Category(String id, String name) { this.id = required(id); this.name = required(name); }
        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static final class Product {
        private final String id;
        private final String name;
        private final double unitPrice;
        private final Category category;
        private int stock;
        private boolean active = true;
        public Product(String id, String name, double unitPrice, Category category, int initialStock) {
            this.id = required(id); this.name = required(name); this.unitPrice = nonNegative(unitPrice); this.category = category;
            if (initialStock < 0) throw new IllegalArgumentException("Stock cannot be negative");
            this.stock = initialStock;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public double getUnitPrice() { return unitPrice; }
        public Category getCategory() { return category; }
        public int getStock() { return stock; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public void increaseStock(int quantity) { if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative"); stock += quantity; }
        public void decreaseStock(int quantity) { if (quantity < 0 || quantity > stock) throw new IllegalArgumentException("Insufficient stock"); stock -= quantity; }
    }

    public static final class Customer {
        private final String id; private final String name; private final LoyaltyTier tier;
        public Customer(String id, String name, LoyaltyTier tier) { this.id = required(id); this.name = required(name); this.tier = tier; }
        public String getId() { return id; } public String getName() { return name; } public LoyaltyTier getTier() { return tier; }
    }
    public static final class Supplier {
        private final String id; private final String name;
        public Supplier(String id, String name) { this.id = required(id); this.name = required(name); }
        public String getId() { return id; } public String getName() { return name; }
    }
    public static final class CartItem {
        private final Product product; private int quantity;
        public CartItem(Product product, int quantity) { this.product = product; setQuantity(quantity); }
        public Product getProduct() { return product; } public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.quantity = quantity; }
        public double subtotal() { return product.getUnitPrice() * quantity; }
    }
    public static final class Cart {
        private final String id; private final Customer customer; private final Map<String, CartItem> items = new LinkedHashMap<>();
        public Cart(String id, Customer customer) { this.id = required(id); this.customer = customer; }
        public String getId() { return id; } public Customer getCustomer() { return customer; }
        public Map<String, CartItem> getItems() { return Collections.unmodifiableMap(items); }
        public void add(Product product, int quantity) { if (!product.isActive()) throw new IllegalStateException("Inactive product"); CartItem item = items.get(product.getId()); if (item == null) items.put(product.getId(), new CartItem(product, quantity)); else item.setQuantity(item.getQuantity() + quantity); }
        public void update(String productId, int quantity) { CartItem item = items.get(productId); if (item == null) throw new IllegalArgumentException("Item not found"); item.setQuantity(quantity); }
        public void remove(String productId) { items.remove(productId); }
        public double subtotal() { return items.values().stream().mapToDouble(CartItem::subtotal).sum(); }
    }
    public static final class OrderItem {
        private final Product product; private final int quantity; private final double unitPrice;
        public OrderItem(Product product, int quantity, double unitPrice) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.product = product; this.quantity = quantity; this.unitPrice = nonNegative(unitPrice); }
        public Product getProduct() { return product; } public int getQuantity() { return quantity; } public double getUnitPrice() { return unitPrice; } public double subtotal() { return unitPrice * quantity; }
    }
    public static final class Order {
        private final String id; private final Customer customer; private final List<OrderItem> items; private OrderStatus status = OrderStatus.CREATED; private double promotionDiscount; private double shippingCost;
        public Order(String id, Customer customer, List<OrderItem> items) { if (items == null || items.isEmpty()) throw new IllegalArgumentException("Order needs items"); this.id = required(id); this.customer = customer; this.items = new ArrayList<>(items); }
        public String getId() { return id; } public Customer getCustomer() { return customer; } public List<OrderItem> getItems() { return Collections.unmodifiableList(items); } public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; } public double getPromotionDiscount() { return promotionDiscount; } public double getShippingCost() { return shippingCost; }
        public void setPromotionDiscount(double value) { promotionDiscount = nonNegative(value); } public void setShippingCost(double value) { shippingCost = nonNegative(value); }
        public double subtotal() { return items.stream().mapToDouble(OrderItem::subtotal).sum(); } public double total() { return subtotal() - promotionDiscount + shippingCost; }
    }
    public interface Promotion { double discount(Order order); String name(); }
    public static final class PercentagePromotion implements Promotion {
        private final double percent; private final double threshold;
        public PercentagePromotion(double percent, double threshold) { this.percent = nonNegative(percent); this.threshold = nonNegative(threshold); }
        public double discount(Order order) { return order.subtotal() >= threshold ? order.subtotal() * percent / 100.0 : 0; } public String name() { return "Percentage"; }
    }
    public static final class FixedPromotion implements Promotion {
        private final double amount; private final double threshold; private final LoyaltyTier tier;
        public FixedPromotion(double amount, double threshold, LoyaltyTier tier) { this.amount = nonNegative(amount); this.threshold = nonNegative(threshold); this.tier = tier; }
        public double discount(Order order) { return order.subtotal() >= threshold && order.getCustomer().getTier() == tier ? Math.min(amount, order.subtotal()) : 0; } public String name() { return "Fixed"; }
    }
    public static final class BuyXGetYPromotion implements Promotion {
        private final Product product; private final int buy; private final int free;
        public BuyXGetYPromotion(Product product, int buy, int free) { if (buy <= 0 || free <= 0) throw new IllegalArgumentException("Invalid promotion"); this.product = product; this.buy = buy; this.free = free; }
        public double discount(Order order) { for (OrderItem item : order.getItems()) if (item.getProduct().getId().equals(product.getId())) return (item.getQuantity() / (buy + free)) * free * item.getUnitPrice(); return 0; } public String name() { return "BuyXGetY"; }
    }
    public static final class ReturnItem {
        private final Product product; private final int quantity;
        public ReturnItem(Product product, int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.product = product; this.quantity = quantity; }
        public Product getProduct() { return product; } public int getQuantity() { return quantity; }
    }
    public static final class ReturnRequest {
        private final String id; private final Order order; private final List<ReturnItem> items = new ArrayList<>(); private boolean processed;
        public ReturnRequest(String id, Order order) { this.id = required(id); this.order = order; }
        public String getId() { return id; } public Order getOrder() { return order; } public List<ReturnItem> getItems() { return Collections.unmodifiableList(items); } public boolean isProcessed() { return processed; } public void markProcessed() { processed = true; }
        public void add(ReturnItem item) { if (processed) throw new IllegalStateException("Return processed"); items.add(item); } public double total() { return items.stream().mapToDouble(i -> priceInOrder(i.getProduct(), i.getQuantity())).sum(); }
        private double priceInOrder(Product p, int q) { return order.getItems().stream().filter(i -> i.getProduct().getId().equals(p.getId())).mapToDouble(i -> i.getUnitPrice() * q).findFirst().orElseThrow(() -> new IllegalArgumentException("Product not in order")); }
    }
    public static final class Restock {
        private final String id; private final Product product; private final Supplier supplier; private final int quantity; private final double unitCost;
        public Restock(String id, Product product, Supplier supplier, int quantity, double unitCost) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.id = required(id); this.product = product; this.supplier = supplier; this.quantity = quantity; this.unitCost = nonNegative(unitCost); }
        public String getId() { return id; } public Product getProduct() { return product; } public Supplier getSupplier() { return supplier; } public int getQuantity() { return quantity; } public double totalCost() { return quantity * unitCost; }
    }
    public static final class CashflowEntry {
        private final CashflowType type; private final String description; private final double amount; private final LocalDateTime timestamp = LocalDateTime.now();
        public CashflowEntry(CashflowType type, String description, double amount) { this.type = type; this.description = required(description); this.amount = nonNegative(amount); }
        public CashflowType getType() { return type; } public String getDescription() { return description; } public double getAmount() { return amount; } public LocalDateTime getTimestamp() { return timestamp; }
    }
    private static String required(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("Value is required"); return value; }
    private static double nonNegative(double value) { if (value < 0) throw new IllegalArgumentException("Value cannot be negative"); return value; }
}
