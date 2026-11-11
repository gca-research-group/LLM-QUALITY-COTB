package store.model;

import java.util.ArrayList;
import java.util.List;

public final class Models {
    private Models() {}

    public enum LoyaltyTier { REGULAR, SILVER, GOLD }
    public enum OrderStatus { CREATED, PAID, CANCELLED, PARTIALLY_RETURNED, RETURNED }
    public enum PaymentMethod { CASH, CARD, PIX }
    public enum CashflowType { INFLOW, OUTFLOW }
    public enum ShippingType { STANDARD, EXPRESS, PICKUP }

    public static final class Category {
        private final String id;
        private final String name;
        public Category(String id, String name) { this.id = require(id); this.name = require(name); }
        public String getId() { return id; }
        public String getName() { return name; }
        private static String require(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("Value is required"); return value; }
    }

    public static final class Product {
        private final String id;
        private final String name;
        private final double unitPrice;
        private final Category category;
        private boolean active = true;
        private int stock;
        public Product(String id, String name, double unitPrice, Category category) {
            if (unitPrice < 0) throw new IllegalArgumentException("Price cannot be negative");
            this.id = require(id); this.name = require(name); this.unitPrice = unitPrice; this.category = category;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        public double getUnitPrice() { return unitPrice; }
        public Category getCategory() { return category; }
        public boolean isActive() { return active; }
        public int getStock() { return stock; }
        public void setActive(boolean active) { this.active = active; }
        public void increaseStock(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); stock += quantity; }
        public void decreaseStock(int quantity) { if (quantity <= 0 || quantity > stock) throw new IllegalArgumentException("Invalid stock decrease"); stock -= quantity; }
        private static String require(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("Value is required"); return value; }
    }

    public static final class Customer {
        private final String id; private final String name; private final LoyaltyTier tier;
        public Customer(String id, String name, LoyaltyTier tier) { this.id = id; this.name = name; this.tier = tier; }
        public String getId() { return id; } public String getName() { return name; } public LoyaltyTier getTier() { return tier; }
    }

    public static final class Supplier {
        private final String id; private final String name;
        public Supplier(String id, String name) { this.id = id; this.name = name; }
        public String getId() { return id; } public String getName() { return name; }
    }

    public static final class CartItem {
        private final Product product; private int quantity;
        public CartItem(Product product, int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.product = product; this.quantity = quantity; }
        public Product getProduct() { return product; } public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.quantity = quantity; }
        public double subtotal() { return product.getUnitPrice() * quantity; }
    }

    public static final class Cart {
        private final String id; private final Customer customer; private final List<CartItem> items = new ArrayList<>();
        public Cart(String id, Customer customer) { this.id = id; this.customer = customer; }
        public String getId() { return id; } public Customer getCustomer() { return customer; } public List<CartItem> getItems() { return List.copyOf(items); }
        public void add(Product product, int quantity) { CartItem existing = find(product.getId()); if (existing == null) items.add(new CartItem(product, quantity)); else existing.setQuantity(existing.getQuantity() + quantity); }
        public void update(String productId, int quantity) { CartItem item = find(productId); if (item == null) throw new IllegalArgumentException("Product not in cart"); item.setQuantity(quantity); }
        public void remove(String productId) { items.removeIf(item -> item.getProduct().getId().equals(productId)); }
        public double subtotal() { return items.stream().mapToDouble(CartItem::subtotal).sum(); }
        private CartItem find(String productId) { return items.stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null); }
    }
}
