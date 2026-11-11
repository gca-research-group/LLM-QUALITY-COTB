package store.service;

import java.io.*;
import java.util.*;
import store.model.*;
import store.repository.StoreRepository;

public class StoreService {
    private final StoreRepository repo;

    public StoreService(StoreRepository repo) {
        this.repo = repo;
    }

    public void addCategory(Category category) {
        if (repo.getCategories().putIfAbsent(category.getId(), category) != null) {
            throw new IllegalArgumentException("Duplicate category");
        }
    }

    public Category findCategoryById(String id) {
        Category category = repo.getCategories().get(id);
        if (category == null) throw new IllegalArgumentException("Category not found");
        return category;
    }

    public void addProduct(Product product) {
        if (repo.getProducts().putIfAbsent(product.getId(), product) != null) {
            throw new IllegalArgumentException("Duplicate product identifier");
        }
    }

    public Product findProductById(String id) {
        Product product = repo.getProducts().get(id);
        if (product == null) throw new IllegalArgumentException("Product not found");
        return product;
    }

    public List<Product> findProductsByName(String name) {
        List<Product> result = new ArrayList<>();

        for (Product product : repo.getProducts().values()) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(product);
            }
        }

        return result;
    }

    public List<Product> listProductsByCategory(String categoryId) {
        List<Product> result = new ArrayList<>();

        for (Product product : repo.getProducts().values()) {
            if (product.getCategory().getId().equals(categoryId)) {
                result.add(product);
            }
        }

        return result;
    }

    public void setProductActive(String productId, boolean active) {
        findProductById(productId).setActive(active);
    }

    public void increaseStock(String productId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        findProductById(productId);

        repo.getStock().put(
                productId,
                repo.getStock().getOrDefault(productId, 0) + quantity
        );
    }

    public void decreaseStock(String productId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");

        findProductById(productId);

        int current = repo.getStock().getOrDefault(productId, 0);

        if (quantity > current) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        repo.getStock().put(productId, current - quantity);
    }

    public int getStock(String productId) {
        return repo.getStock().getOrDefault(productId, 0);
    }

    public void addCustomer(Customer customer) {
        if (repo.getCustomers().putIfAbsent(customer.getId(), customer) != null) {
            throw new IllegalArgumentException("Duplicate customer identifier");
        }
    }

    public void addSupplier(Supplier supplier) {
        if (repo.getSuppliers().putIfAbsent(supplier.getId(), supplier) != null) {
            throw new IllegalArgumentException("Duplicate supplier identifier");
        }
    }

    public Cart createCart(String id, String customerId) {
        if (repo.getCarts().containsKey(id)) {
            throw new IllegalArgumentException("Duplicate cart identifier");
        }

        Customer customer = repo.getCustomers().get(customerId);

        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Cart cart = new Cart(id, customer);
        repo.getCarts().put(id, cart);

        return cart;
    }

    public void addToCart(String cartId, String productId, int quantity) {
        Cart cart = getCart(cartId);
        Product product = findProductById(productId);
        cart.add(product, quantity);
    }

    public void updateCart(String cartId, String productId, int quantity) {
        getCart(cartId).update(productId, quantity);
    }

    public void removeFromCart(String cartId, String productId) {
        getCart(cartId).remove(productId);
    }

    public Cart getCart(String id) {
        Cart cart = repo.getCarts().get(id);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        return cart;
    }

    public Order createOrder(String orderId, String cartId) {
        if (repo.getOrders().containsKey(orderId)) {
            throw new IllegalArgumentException("Duplicate order identifier");
        }

        Cart cart = getCart(cartId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        List<OrderItem> items = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());

            items.add(new OrderItem(
                    cartItem.getProduct(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getUnitPrice()
            ));
        }

        Order order = new Order(orderId, cart.getCustomer(), items);
        repo.getOrders().put(orderId, order);

        return order;
    }

    public Order getOrder(String id) {
        Order order = repo.getOrders().get(id);

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        return order;
    }

    public void applyPromotion(String orderId, Promotion promotion) {
        Order order = getOrder(orderId);
        order.applyPromotion(promotion.calculateDiscount(order));
    }

    public void applyShipping(String orderId, ShippingType type) {
        double cost = switch (type) {
            case STANDARD -> 12.0;
            case EXPRESS -> 25.0;
            case PICKUP -> 0.0;
        };

        getOrder(orderId).applyShipping(cost);
    }

    public void registerPromotion(Promotion promotion) {
        repo.getPromotions().add(promotion);
    }

    public void pay(String paymentId, String orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled orders cannot be paid");
        }

        if (repo.getPayments().containsKey(paymentId)) {
            throw new IllegalArgumentException("Duplicate payment identifier");
        }

        Payment payment = new Payment(
                paymentId,
                orderId,
                order.getFinalTotal()
        );

        order.markPaid();
        repo.getPayments().put(paymentId, payment);

        recordCashflow(new CashflowEntry(
                "CF-P-" + paymentId,
                CashflowType.INFLOW,
                payment.getAmount(),
                "Payment " + paymentId
        ));
    }

    public void cancelOrder(String orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Paid orders cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order already cancelled");
        }

        for (OrderItem item : order.getItems()) {
            increaseStock(item.getProduct().getId(), item.getQuantity());
        }

        order.cancel();
    }

    public ReturnRequest createReturn(String returnId, String orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        if (repo.getReturns().containsKey(returnId)) {
            throw new IllegalArgumentException("Duplicate return identifier");
        }

        ReturnRequest request = new ReturnRequest(returnId, order);
        repo.getReturns().put(returnId, request);

        return request;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        ReturnRequest request = getReturn(returnId);
        request.addItem(findProductById(productId), quantity);
    }

    public ReturnRequest getReturn(String id) {
        ReturnRequest request = repo.getReturns().get(id);

        if (request == null) {
            throw new IllegalArgumentException("Return not found");
        }

        return request;
    }

    public void processRefund(String returnId) {
        ReturnRequest request = getReturn(returnId);

        if (request.isProcessed()) {
            throw new IllegalStateException("Return already processed");
        }

        if (request.getOrder().getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be returned");
        }

        if (request.getItems().isEmpty()) {
            throw new IllegalStateException("Return must contain at least one item");
        }

        double amount = request.getRefundTotal();

        for (ReturnItem item : request.getItems()) {
            increaseStock(item.getProduct().getId(), item.getQuantity());
        }

        request.markProcessed();
        request.getOrder().markRefunded();

        recordCashflow(new CashflowEntry(
                "CF-R-" + returnId,
                CashflowType.OUTFLOW,
                amount,
                "Refund " + returnId
        ));
    }

    public Restock restock(
            String restockId,
            String productId,
            String supplierId,
            int quantity,
            double unitCost) {

        if (repo.getRestocks().containsKey(restockId)) {
            throw new IllegalArgumentException("Duplicate restock identifier");
        }

        Product product = findProductById(productId);
        Supplier supplier = repo.getSuppliers().get(supplierId);

        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }

        Restock restock = new Restock(
                restockId,
                product,
                supplier,
                quantity,
                unitCost
        );

        increaseStock(productId, quantity);
        repo.getRestocks().put(restockId, restock);

        recordCashflow(new CashflowEntry(
                "CF-S-" + restockId,
                CashflowType.OUTFLOW,
                restock.getTotalCost(),
                "Restock " + restockId
        ));

        return restock;
    }

    public void recordCashflow(CashflowEntry entry) {
        if (repo.getCashflows().putIfAbsent(entry.getId(), entry) != null) {
            throw new IllegalArgumentException("Duplicate cashflow identifier");
        }
    }

    public double totalInflow() {
        return repo.getCashflows().values().stream()
                .filter(e -> e.getType() == CashflowType.INFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public double totalOutflow() {
        return repo.getCashflows().values().stream()
                .filter(e -> e.getType() == CashflowType.OUTFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public void exportInventory(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("productId,name,category,stock");

            for (Product product : repo.getProducts().values()) {
                out.printf(
                        Locale.US,
                        "%s,%s,%s,%d%n",
                        product.getId(),
                        product.getName(),
                        product.getCategory().getName(),
                        getStock(product.getId())
                );
            }
        }
    }

    public void exportCashflow(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("id,type,amount,description");

            for (CashflowEntry entry : repo.getCashflows().values()) {
                out.printf(
                        Locale.US,
                        "%s,%s,%.2f,%s%n",
                        entry.getId(),
                        entry.getType(),
                        entry.getAmount(),
                        entry.getDescription()
                );
            }
        }
    }

    public String stockReport() {
        StringBuilder builder = new StringBuilder("STOCK REPORT")
                .append(System.lineSeparator());

        for (Product product : repo.getProducts().values()) {
            builder.append(product.getId())
                    .append(" = ")
                    .append(getStock(product.getId()))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    public String salesByProductReport() {
        Map<String, Integer> sold = new LinkedHashMap<>();

        for (Product product : repo.getProducts().values()) {
            sold.put(product.getId(), 0);
        }

        for (Order order : repo.getOrders().values()) {
            if (order.getStatus() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    sold.merge(
                            item.getProduct().getId(),
                            item.getQuantity(),
                            Integer::sum
                    );
                }
            }
        }

        StringBuilder builder = new StringBuilder("SALES BY PRODUCT REPORT")
                .append(System.lineSeparator());

        sold.forEach((id, quantity) ->
                builder.append(id)
                        .append(" = ")
                        .append(quantity)
                        .append(System.lineSeparator()));

        return builder.toString();
    }

    public String ordersByStatusReport() {
        Map<OrderStatus, Integer> counts = new EnumMap<>(OrderStatus.class);

        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }

        for (Order order : repo.getOrders().values()) {
            counts.merge(order.getStatus(), 1, Integer::sum);
        }

        StringBuilder builder = new StringBuilder("ORDERS BY STATUS REPORT")
                .append(System.lineSeparator());

        counts.forEach((status, count) ->
                builder.append(status)
                        .append(" = ")
                        .append(count)
                        .append(System.lineSeparator()));

        return builder.toString();
    }

    public String cashflowSummaryReport() {
        return String.format(
                Locale.US,
                "CASHFLOW SUMMARY REPORT%n" +
                "Total inflow = %.2f%n" +
                "Total outflow = %.2f%n" +
                "Net cashflow = %.2f%n",
                totalInflow(),
                totalOutflow(),
                totalInflow() - totalOutflow()
        );
    }

    public String returnsReport() {
        StringBuilder builder = new StringBuilder("RETURNS/REFUNDS REPORT")
                .append(System.lineSeparator());

        for (ReturnRequest request : repo.getReturns().values()) {
            builder.append(request.getId())
                    .append(" = ")
                    .append(String.format(Locale.US, "%.2f", request.getRefundTotal()))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    public String restockingReport() {
        StringBuilder builder = new StringBuilder("RESTOCKING REPORT")
                .append(System.lineSeparator());

        for (Restock restock : repo.getRestocks().values()) {
            builder.append(restock.getId())
                    .append(" product=")
                    .append(restock.getProduct().getId())
                    .append(" supplier=")
                    .append(restock.getSupplier().getId())
                    .append(" quantity=")
                    .append(restock.getQuantity())
                    .append(" total=")
                    .append(String.format(Locale.US, "%.2f", restock.getTotalCost()))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    public int productCount() {
        return repo.getProducts().size();
    }

    public int customerCount() {
        return repo.getCustomers().size();
    }

    public int supplierCount() {
        return repo.getSuppliers().size();
    }

    public Map<String, Product> products() {
        return Collections.unmodifiableMap(repo.getProducts());
    }
}