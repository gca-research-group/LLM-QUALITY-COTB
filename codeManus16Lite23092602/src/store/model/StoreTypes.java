package store.model;

import java.util.*;

public final class StoreTypes {
    private StoreTypes() {}

    public enum LoyaltyTier { REGULAR, SILVER, GOLD }
    public enum OrderStatus { CREATED, PAID, CANCELLED, REFUNDED }
    public enum PaymentMethod { CARD, CASH, TRANSFER }
    public enum CashflowType { INFLOW, OUTFLOW }
    public enum ShippingType { STANDARD, EXPRESS, PICKUP }

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
        private boolean active = true;
        private int stock;
        public Product(String id, String name, double unitPrice, Category category) {
            this.id = required(id); this.name = required(name); this.unitPrice = nonNegative(unitPrice); this.category = Objects.requireNonNull(category);
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public double getUnitPrice() { return unitPrice; }
        public Category getCategory() { return category; }
        public boolean isActive() { return active; }
        public int getStock() { return stock; }
        public void setActive(boolean active) { this.active = active; }
        public void increaseStock(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive"); stock += quantity; }
        public void decreaseStock(int quantity) { if (quantity <= 0 || quantity > stock) throw new IllegalArgumentException("insufficient stock or invalid quantity"); stock -= quantity; }
    }

    public static final class Customer {
        private final String id;
        private final String name;
        private final LoyaltyTier loyaltyTier;
        public Customer(String id, String name, LoyaltyTier loyaltyTier) { this.id = required(id); this.name = required(name); this.loyaltyTier = Objects.requireNonNull(loyaltyTier); }
        public String getId() { return id; }
        public String getName() { return name; }
        public LoyaltyTier getLoyaltyTier() { return loyaltyTier; }
    }

    public static final class Supplier {
        private final String id;
        private final String name;
        public Supplier(String id, String name) { this.id = required(id); this.name = required(name); }
        public String getId() { return id; }
        public String getName() { return name; }
    }

    public static final class CartItem {
        private final Product product;
        private int quantity;
        public CartItem(Product product, int quantity) { this.product = Objects.requireNonNull(product); setQuantity(quantity); }
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("cart quantity must be positive"); this.quantity = quantity; }
        public double subtotal() { return product.getUnitPrice() * quantity; }
    }

    public static final class Cart {
        private final String id;
        private final Customer customer;
        private final Map<String, CartItem> items = new LinkedHashMap<>();
        public Cart(String id, Customer customer) { this.id = required(id); this.customer = Objects.requireNonNull(customer); }
        public String getId() { return id; }
        public Customer getCustomer() { return customer; }
        public Collection<CartItem> getItems() { return Collections.unmodifiableCollection(items.values()); }
        public void add(Product product, int quantity) { if (!product.isActive()) throw new IllegalArgumentException("inactive product"); if (quantity <= 0) throw new IllegalArgumentException("cart quantity must be positive"); items.compute(product.getId(), (k, v) -> { if (v == null) return new CartItem(product, quantity); v.setQuantity(v.getQuantity() + quantity); return v; }); }
        public void update(String productId, int quantity) { CartItem item = items.get(productId); if (item == null) throw new IllegalArgumentException("product not in cart"); item.setQuantity(quantity); }
        public void remove(String productId) { if (items.remove(productId) == null) throw new IllegalArgumentException("product not in cart"); }
        public double subtotal() { return items.values().stream().mapToDouble(CartItem::subtotal).sum(); }
        public boolean isEmpty() { return items.isEmpty(); }
    }

    public static final class OrderItem {
        private final Product product;
        private final int quantity;
        private final double unitPrice;
        public OrderItem(Product product, int quantity, double unitPrice) { if (quantity <= 0) throw new IllegalArgumentException("order quantity must be positive"); this.product = Objects.requireNonNull(product); this.quantity = quantity; this.unitPrice = nonNegative(unitPrice); }
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
        private double shipping;
        private OrderStatus status = OrderStatus.CREATED;
        public Order(String id, Customer customer, List<OrderItem> items) { this.id = required(id); this.customer = Objects.requireNonNull(customer); if (items == null || items.isEmpty()) throw new IllegalArgumentException("order must contain items"); this.items = List.copyOf(items); }
        public String getId() { return id; }
        public Customer getCustomer() { return customer; }
        public List<OrderItem> getItems() { return items; }
        public double subtotal() { return items.stream().mapToDouble(OrderItem::subtotal).sum(); }
        public double getPromotionDiscount() { return promotionDiscount; }
        public double getShipping() { return shipping; }
        public double total() { return subtotal() - promotionDiscount + shipping; }
        public OrderStatus getStatus() { return status; }
        public void setPromotionDiscount(double value) { if (value < 0 || value > subtotal()) throw new IllegalArgumentException("invalid discount"); promotionDiscount = value; }
        public void setShipping(double value) { shipping = nonNegative(value); }
        public void markPaid() { if (status == OrderStatus.CANCELLED) throw new IllegalStateException("cancelled order"); status = OrderStatus.PAID; }
        public void cancel() { if (status == OrderStatus.PAID) throw new IllegalStateException("paid order cannot be cancelled"); if (status == OrderStatus.CANCELLED) throw new IllegalStateException("already cancelled"); status = OrderStatus.CANCELLED; }
        public void markRefunded() { if (status != OrderStatus.PAID) throw new IllegalStateException("only paid order can be refunded"); status = OrderStatus.REFUNDED; }
    }

    public static final class Payment {
        private final String id; private final String orderId; private final double amount; private final PaymentMethod method;
        public Payment(String id, String orderId, double amount, PaymentMethod method) { this.id = required(id); this.orderId = required(orderId); this.amount = nonNegative(amount); this.method = Objects.requireNonNull(method); }
        public String getId() { return id; } public String getOrderId() { return orderId; } public double getAmount() { return amount; } public PaymentMethod getMethod() { return method; }
    }

    public static final class ReturnRequest {
        private final String id; private final Order order; private final Map<String, Integer> quantities = new LinkedHashMap<>(); private double refundTotal; private boolean processed;
        public ReturnRequest(String id, Order order) { this.id = required(id); this.order = Objects.requireNonNull(order); if (order.getStatus() != OrderStatus.PAID) throw new IllegalArgumentException("only paid orders can be returned"); }
        public String getId() { return id; } public Order getOrder() { return order; } public Map<String,Integer> getQuantities() { return Collections.unmodifiableMap(quantities); } public double getRefundTotal() { return refundTotal; } public boolean isProcessed() { return processed; }
        public void add(String productId, int quantity) { if (processed || quantity <= 0) throw new IllegalArgumentException("invalid return"); int bought = order.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToInt(OrderItem::getQuantity).sum(); int current = quantities.getOrDefault(productId, 0); if (bought == 0 || current + quantity > bought) throw new IllegalArgumentException("returned quantity exceeds bought quantity"); quantities.put(productId, current + quantity); }
        public void calculate() { if (quantities.isEmpty()) throw new IllegalArgumentException("return must contain items"); refundTotal = quantities.entrySet().stream().mapToDouble(e -> order.getItems().stream().filter(i -> i.getProduct().getId().equals(e.getKey())).findFirst().orElseThrow().getUnitPrice() * e.getValue()).sum(); }
        public void markProcessed() { processed = true; }
    }

    public static final class RestockEntry {
        private final String id, productId, supplierId; private final int quantity; private final double unitCost;
        public RestockEntry(String id, String productId, String supplierId, int quantity, double unitCost) { this.id = required(id); this.productId = required(productId); this.supplierId = required(supplierId); if (quantity <= 0) throw new IllegalArgumentException("restock quantity must be positive"); this.quantity = quantity; this.unitCost = nonNegative(unitCost); }
        public String getId() { return id; } public String getProductId() { return productId; } public String getSupplierId() { return supplierId; } public int getQuantity() { return quantity; } public double totalCost() { return quantity * unitCost; }
    }

    public static final class CashflowEntry {
        private final String id, description; private final CashflowType type; private final double amount;
        public CashflowEntry(String id, CashflowType type, double amount, String description) { this.id = required(id); this.type = Objects.requireNonNull(type); this.amount = nonNegative(amount); this.description = required(description); }
        public String getId() { return id; } public CashflowType getType() { return type; } public double getAmount() { return amount; } public String getDescription() { return description; }
    }

    private static String required(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("value is required"); return value; }
    private static double nonNegative(double value) { if (value < 0) throw new IllegalArgumentException("value cannot be negative"); return value; }
}
