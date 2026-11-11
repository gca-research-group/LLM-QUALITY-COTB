package store.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Model {
    private Model() { }

    public enum LoyaltyTier { REGULAR, SILVER, GOLD }
    public enum OrderStatus { CREATED, PAID, CANCELLED, PARTIALLY_RETURNED, RETURNED }
    public enum PaymentMethod { CASH, CARD, PIX }
    public enum ShippingType { STANDARD, EXPRESS, PICKUP }
    public enum CashflowType { INFLOW, OUTFLOW }

    public static final class Category {
        private final String id;
        private final String name;
        public Category(String id, String name) {
            this.id = requireText(id, "category id");
            this.name = requireText(name, "category name");
        }
        public String getId() { return id; }
        public String getName() { return name; }
        @Override public String toString() { return id + " - " + name; }
    }

    public static final class Product {
        private final String id;
        private final String name;
        private final double unitPrice;
        private final Category category;
        private boolean active = true;
        private int stock;
        public Product(String id, String name, double unitPrice, Category category) {
            this.id = requireText(id, "product id");
            this.name = requireText(name, "product name");
            this.unitPrice = requireNonNegative(unitPrice, "unit price");
            if (category == null) throw new IllegalArgumentException("category is required");
            this.category = category;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public double getUnitPrice() { return unitPrice; }
        public Category getCategory() { return category; }
        public boolean isActive() { return active; }
        public int getStock() { return stock; }
        public void setActive(boolean active) { this.active = active; }
        public void increaseStock(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive"); stock += quantity; }
        public void decreaseStock(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive"); if (quantity > stock) throw new IllegalArgumentException("insufficient stock"); stock -= quantity; }
    }

    public static final class Customer {
        private final String id;
        private final String name;
        private final LoyaltyTier loyaltyTier;
        public Customer(String id, String name, LoyaltyTier loyaltyTier) {
            this.id = requireText(id, "customer id"); this.name = requireText(name, "customer name");
            if (loyaltyTier == null) throw new IllegalArgumentException("loyalty tier is required");
            this.loyaltyTier = loyaltyTier;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public LoyaltyTier getLoyaltyTier() { return loyaltyTier; }
    }

    public static final class Supplier {
        private final String id;
        private final String name;
        public Supplier(String id, String name) { this.id = requireText(id, "supplier id"); this.name = requireText(name, "supplier name"); }
        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static final class Cart {
        private final String id;
        private final Customer customer;
        private final Map<String, Integer> items = new LinkedHashMap<>();
        public Cart(String id, Customer customer) { this.id = requireText(id, "cart id"); if (customer == null) throw new IllegalArgumentException("customer is required"); this.customer = customer; }
        public String getId() { return id; }
        public Customer getCustomer() { return customer; }
        public Map<String, Integer> getItems() { return new LinkedHashMap<>(items); }
        public void put(String productId, int quantity) { if (quantity <= 0) throw new IllegalArgumentException("cart quantity must be positive"); items.put(productId, quantity); }
        public void remove(String productId) { items.remove(productId); }
        public boolean isEmpty() { return items.isEmpty(); }
    }

    public static final class OrderItem {
        private final Product product;
        private final int quantity;
        private final double unitPrice;
        public OrderItem(Product product, int quantity, double unitPrice) { this.product = product; this.quantity = quantity; this.unitPrice = unitPrice; }
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double subtotal() { return quantity * unitPrice; }
    }

    public static final class Order {
        private final String id;
        private final Customer customer;
        private final List<OrderItem> items;
        private double promotionDiscount;
        private double shippingCost;
        private OrderStatus status = OrderStatus.CREATED;
        public Order(String id, Customer customer, List<OrderItem> items) { this.id = id; this.customer = customer; this.items = new ArrayList<>(items); }
        public String getId() { return id; }
        public Customer getCustomer() { return customer; }
        public List<OrderItem> getItems() { return new ArrayList<>(items); }
        public double subtotal() { return items.stream().mapToDouble(OrderItem::subtotal).sum(); }
        public double getPromotionDiscount() { return promotionDiscount; }
        public void setPromotionDiscount(double value) { promotionDiscount = requireNonNegative(value, "discount"); }
        public double getShippingCost() { return shippingCost; }
        public void setShippingCost(double value) { shippingCost = requireNonNegative(value, "shipping"); }
        public double total() { return subtotal() - promotionDiscount + shippingCost; }
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    public static final class ReturnRequest {
        private final String id;
        private final Order order;
        private final Map<String, Integer> items = new LinkedHashMap<>();
        private double refundTotal;
        private boolean processed;
        public ReturnRequest(String id, Order order) { this.id = id; this.order = order; }
        public String getId() { return id; }
        public Order getOrder() { return order; }
        public Map<String, Integer> getItems() { return new LinkedHashMap<>(items); }
        public void add(String productId, int quantity) { if (quantity <= 0) throw new IllegalArgumentException("returned quantity must be positive"); items.merge(productId, quantity, Integer::sum); }
        public double getRefundTotal() { return refundTotal; }
        public void setRefundTotal(double value) { refundTotal = value; }
        public boolean isProcessed() { return processed; }
        public void setProcessed(boolean value) { processed = value; }
    }

    public static final class Restock {
        private final String id;
        private final Product product;
        private final Supplier supplier;
        private final int quantity;
        private final double unitCost;
        public Restock(String id, Product product, Supplier supplier, int quantity, double unitCost) { this.id = id; this.product = product; this.supplier = supplier; this.quantity = quantity; this.unitCost = requireNonNegative(unitCost, "restock unit cost"); }
        public String getId() { return id; }
        public Product getProduct() { return product; }
        public Supplier getSupplier() { return supplier; }
        public int getQuantity() { return quantity; }
        public double getUnitCost() { return unitCost; }
        public double totalCost() { return quantity * unitCost; }
    }

    public static final class CashflowEntry {
        private final String id;
        private final CashflowType type;
        private final double amount;
        private final String description;
        public CashflowEntry(String id, CashflowType type, double amount, String description) { this.id = id; this.type = type; this.amount = requireNonNegative(amount, "cashflow amount"); this.description = description; }
        public String getId() { return id; }
        public CashflowType getType() { return type; }
        public double getAmount() { return amount; }
        public String getDescription() { return description; }
    }

    public static double requireNonNegative(double value, String label) { if (value < 0) throw new IllegalArgumentException(label + " cannot be negative"); return value; }
    public static String requireText(String value, String label) { if (value == null || value.isBlank()) throw new IllegalArgumentException(label + " is required"); return value; }
}
