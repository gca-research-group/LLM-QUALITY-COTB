package store.service;

import store.model.Cart;
import store.model.CartItem;
import store.model.CashflowEntry;
import store.model.Customer;
import store.model.Order;
import store.model.Payment;
import store.model.Product;
import store.model.Promotion;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.ShippingRule;
import store.model.Supplier;
import store.repository.StoreRepository;

import java.util.ArrayList;
import java.util.List;

public class StoreService {
    private final StoreRepository repository;

    public StoreService(StoreRepository repository) {
        this.repository = repository;
    }

    public void registerProduct(Product product) {
        repository.getCatalog().addProduct(product);
    }

    public Product findProduct(String id) {
        return repository.getCatalog().findProduct(id);
    }

    public List<Product> searchProductsByName(String name) {
        return repository.getCatalog().searchByName(name);
    }

    public List<Product> listProductsByCategory(String categoryId) {
        return repository.getCatalog().byCategory(categoryId);
    }

    public void activateProduct(String productId) {
        requireProduct(productId).setActive(true);
    }

    public void deactivateProduct(String productId) {
        requireProduct(productId).setActive(false);
    }

    public void registerCustomer(Customer customer) {
        repository.addCustomer(customer);
    }

    public void registerSupplier(Supplier supplier) {
        repository.addSupplier(supplier);
    }

    public void registerPromotion(String id, Promotion promotion) {
        repository.addPromotion(id, promotion);
    }

    public void registerShippingRule(String id, ShippingRule rule) {
        repository.addShippingRule(id, rule);
    }

    public void initializeStock(String productId, int quantity) {
        requireProduct(productId);

        if (quantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        repository.setInitialStock(productId, quantity);
    }

    public void increaseStock(String productId, int quantity) {
        requireProduct(productId);
        repository.increaseStock(productId, quantity);
    }

    public void decreaseStock(String productId, int quantity) {
        requireProduct(productId);
        repository.decreaseStock(productId, quantity);
    }

    public Cart createCart(String id, String customerId) {
        Customer customer = requireCustomer(customerId);
        Cart cart = new Cart(id, customer);
        repository.addCart(cart);
        return cart;
    }

    public void addToCart(String cartId, String productId, int quantity) {
        Cart cart = requireCart(cartId);
        Product product = requireProduct(productId);
        cart.add(product, quantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        requireCart(cartId).update(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        requireCart(cartId).remove(productId);
    }

    public Order createOrder(String orderId, String cartId) {
        Cart cart = requireCart(cartId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        for (CartItem item : cart.getItems()) {
            if (repository.stock(item.getProduct().getId()) < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for " + item.getProduct().getId());
            }
        }

        List<Order.OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            repository.decreaseStock(item.getProduct().getId(), item.getQuantity());

            orderItems.add(new Order.OrderItem(
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getUnitPrice()
            ));
        }

        Order order = new Order(orderId, cart.getCustomer(), orderItems);
        repository.addOrder(order);

        return order;
    }

    public void applyPromotion(String orderId, String promotionId) {
        Order order = requireOrder(orderId);
        Promotion promotion = repository.promotion(promotionId);

        if (promotion == null) {
            throw new IllegalArgumentException("Promotion not found");
        }

        order.setPromotionDiscount(promotion.discount(order));
    }

    public void applyShipping(String orderId, String shippingId) {
        Order order = requireOrder(orderId);
        ShippingRule rule = repository.shippingRule(shippingId);

        if (rule == null) {
            throw new IllegalArgumentException("Shipping rule not found");
        }

        order.setShippingCost(rule.cost(order));
    }

    public Payment pay(String paymentId, String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }

        if (order.getStatus() == Order.Status.PAID) {
            throw new IllegalStateException("Order already paid");
        }

        Payment payment = new Payment(
                paymentId,
                orderId,
                order.finalTotal(),
                true
        );

        repository.addPayment(payment);
        order.setStatus(Order.Status.PAID);

        repository.addCashflow(new CashflowEntry(
                "CF-PAY-" + paymentId,
                CashflowEntry.Type.INFLOW,
                payment.getAmount(),
                "Payment for " + orderId
        ));

        return payment;
    }

    public void cancel(String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() == Order.Status.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }

        if (order.getStatus() == Order.Status.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        for (Order.OrderItem item : order.getItems()) {
            repository.increaseStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus(Order.Status.CANCELLED);
    }

    public ReturnRequest createReturn(String returnId, String orderId) {
        Order order = requireOrder(orderId);

        if (order.getStatus() != Order.Status.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        ReturnRequest returnRequest = new ReturnRequest(returnId, orderId);
        repository.addReturn(returnRequest);

        return returnRequest;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        ReturnRequest returnRequest = requireReturn(returnId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Order order = requireOrder(returnRequest.getOrderId());
        Order.OrderItem purchasedItem = null;

        for (Order.OrderItem item : order.getItems()) {
            if (item.getProductId().equals(productId)) {
                purchasedItem = item;
                break;
            }
        }

        if (purchasedItem == null) {
            throw new IllegalArgumentException("Product was not bought in order");
        }

        int alreadyReturned = 0;

        for (ReturnRequest.ReturnedItem item : returnRequest.getItems()) {
            if (item.getProductId().equals(productId)) {
                alreadyReturned += item.getQuantity();
            }
        }

        if (alreadyReturned + quantity > purchasedItem.getQuantity()) {
            throw new IllegalArgumentException("Returned quantity exceeds purchased quantity");
        }

        returnRequest.add(new ReturnRequest.ReturnedItem(
                productId,
                quantity,
                purchasedItem.getUnitPrice()
        ));
    }

    public double processRefund(String returnId) {
        ReturnRequest returnRequest = requireReturn(returnId);

        if (returnRequest.isProcessed()) {
            throw new IllegalStateException("Return already processed");
        }

        if (returnRequest.getItems().isEmpty()) {
            throw new IllegalStateException("Return must contain at least one item");
        }

        for (ReturnRequest.ReturnedItem item : returnRequest.getItems()) {
            repository.increaseStock(item.getProductId(), item.getQuantity());
        }

        double refund = returnRequest.refundTotal();

        returnRequest.markProcessed();

        repository.addCashflow(new CashflowEntry(
                "CF-REF-" + returnRequest.getId(),
                CashflowEntry.Type.OUTFLOW,
                refund,
                "Refund for " + returnRequest.getOrderId()
        ));

        Order order = requireOrder(returnRequest.getOrderId());
        boolean fullyReturned = true;

        for (Order.OrderItem orderItem : order.getItems()) {
            int returned = 0;

            for (ReturnRequest.ReturnedItem returnedItem : returnRequest.getItems()) {
                if (returnedItem.getProductId().equals(orderItem.getProductId())) {
                    returned += returnedItem.getQuantity();
                }
            }

            if (returned < orderItem.getQuantity()) {
                fullyReturned = false;
            }
        }

        if (fullyReturned) {
            order.setStatus(Order.Status.REFUNDED);
        }

        return refund;
    }

    public Restock restock(
            String id,
            String productId,
            String supplierId,
            int quantity,
            double unitCost) {

        requireProduct(productId);

        Supplier supplier = repository.supplier(supplierId);

        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }

        Restock restock = new Restock(
                id,
                productId,
                supplierId,
                quantity,
                unitCost
        );

        repository.addRestock(restock);
        repository.increaseStock(productId, quantity);

        repository.addCashflow(new CashflowEntry(
                "CF-RESTOCK-" + id,
                CashflowEntry.Type.OUTFLOW,
                restock.totalCost(),
                "Restock " + productId
        ));

        return restock;
    }

    public void manualOutflow(String id, double amount, String description) {
        repository.addCashflow(new CashflowEntry(
                id,
                CashflowEntry.Type.OUTFLOW,
                amount,
                description
        ));
    }

    private Product requireProduct(String id) {
        Product product = repository.getCatalog().findProduct(id);

        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }

        return product;
    }

    private Customer requireCustomer(String id) {
        Customer customer = repository.customer(id);

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }

        return customer;
    }

    private Cart requireCart(String id) {
        Cart cart = repository.cart(id);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found: " + id);
        }

        return cart;
    }

    private Order requireOrder(String id) {
        Order order = repository.order(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + id);
        }

        return order;
    }

    private ReturnRequest requireReturn(String id) {
        ReturnRequest returnRequest = repository.returnRequest(id);

        if (returnRequest == null) {
            throw new IllegalArgumentException("Return not found: " + id);
        }

        return returnRequest;
    }
}