package store.model;

import java.util.ArrayList;
import java.util.List;
import store.model.Models.CashflowType;
import store.model.Models.Customer;
import store.model.Models.OrderStatus;
import store.model.Models.PaymentMethod;
import store.model.Models.Product;
import store.model.Models.Supplier;

public final class Transactions {
    private Transactions() {}

    public static final class OrderItem {
        private final Product product; private final int quantity; private final double unitPrice;
        public OrderItem(Product product, int quantity, double unitPrice) { if (quantity <= 0 || unitPrice < 0) throw new IllegalArgumentException("Invalid order item"); this.product = product; this.quantity = quantity; this.unitPrice = unitPrice; }
        public Product getProduct() { return product; } public int getQuantity() { return quantity; } public double getUnitPrice() { return unitPrice; }
        public double subtotal() { return quantity * unitPrice; }
    }

    public static final class Order {
        private final String id; private final Customer customer; private final List<OrderItem> items; private double promotionDiscount; private double shipping; private OrderStatus status = OrderStatus.CREATED;
        public Order(String id, Customer customer, List<OrderItem> items) { if (items.isEmpty()) throw new IllegalArgumentException("Order needs items"); this.id = id; this.customer = customer; this.items = new ArrayList<>(items); }
        public String getId() { return id; } public Customer getCustomer() { return customer; } public List<OrderItem> getItems() { return List.copyOf(items); } public OrderStatus getStatus() { return status; }
        public double subtotal() { return items.stream().mapToDouble(OrderItem::subtotal).sum(); }
        public double getPromotionDiscount() { return promotionDiscount; } public double getShipping() { return shipping; }
        public void setPromotionDiscount(double value) { if (value < 0) throw new IllegalArgumentException("Discount cannot be negative"); promotionDiscount = Math.min(value, subtotal()); }
        public void setShipping(double value) { if (value < 0) throw new IllegalArgumentException("Shipping cannot be negative"); shipping = value; }
        public double total() { return subtotal() - promotionDiscount + shipping; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    public interface Promotion { double discount(Order order); }
    public static final class PercentagePromotion implements Promotion { private final double percent; private final double threshold; public PercentagePromotion(double percent, double threshold) { this.percent = percent; this.threshold = threshold; } public double discount(Order order) { return order.subtotal() >= threshold ? order.subtotal() * percent / 100.0 : 0; } }
    public static final class FixedPromotion implements Promotion { private final double value; private final double threshold; private final String requiredTier; public FixedPromotion(double value, double threshold, String requiredTier) { this.value = value; this.threshold = threshold; this.requiredTier = requiredTier; } public double discount(Order order) { return order.subtotal() >= threshold && order.getCustomer().getTier().name().equals(requiredTier) ? value : 0; } }
    public static final class BuyXGetYPromotion implements Promotion { private final String productId; private final int buy; private final int free; public BuyXGetYPromotion(String productId, int buy, int free) { this.productId = productId; this.buy = buy; this.free = free; } public double discount(Order order) { return order.getItems().stream().filter(i -> i.getProduct().getId().equals(productId)).mapToDouble(i -> (i.getQuantity() / (buy + free)) * free * i.getUnitPrice()).sum(); } }

    public static final class Payment { private final String id; private final double amount; private final PaymentMethod method; public Payment(String id, double amount, PaymentMethod method) { if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative"); this.id = id; this.amount = amount; this.method = method; } public String getId() { return id; } public double getAmount() { return amount; } public PaymentMethod getMethod() { return method; } }
    public static final class ReturnItem { private final Product product; private final int quantity; public ReturnItem(Product product, int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); this.product = product; this.quantity = quantity; } public Product getProduct() { return product; } public int getQuantity() { return quantity; } }
    public static final class ReturnRequest { private final String id; private final Order order; private final List<ReturnItem> items = new ArrayList<>(); private boolean processed; private double refundTotal; public ReturnRequest(String id, Order order) { this.id = id; this.order = order; } public String getId() { return id; } public Order getOrder() { return order; } public List<ReturnItem> getItems() { return List.copyOf(items); } public boolean isProcessed() { return processed; } public double getRefundTotal() { return refundTotal; } public void add(ReturnItem item) { items.add(item); } public void process(double total) { processed = true; refundTotal = total; } }
    public static final class Restock { private final String id; private final Product product; private final Supplier supplier; private final int quantity; private final double unitCost; public Restock(String id, Product product, Supplier supplier, int quantity, double unitCost) { if (quantity <= 0 || unitCost < 0) throw new IllegalArgumentException("Invalid restock"); this.id = id; this.product = product; this.supplier = supplier; this.quantity = quantity; this.unitCost = unitCost; } public String getId() { return id; } public Product getProduct() { return product; } public Supplier getSupplier() { return supplier; } public int getQuantity() { return quantity; } public double getTotalCost() { return quantity * unitCost; } }
    public static final class CashflowEntry { private final String id; private final CashflowType type; private final double amount; private final String description; public CashflowEntry(String id, CashflowType type, double amount, String description) { if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative"); this.id = id; this.type = type; this.amount = amount; this.description = description; } public String getId() { return id; } public CashflowType getType() { return type; } public double getAmount() { return amount; } public String getDescription() { return description; } }
}
