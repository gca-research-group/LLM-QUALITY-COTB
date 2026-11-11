package com.store.returns;

import com.store.cashflow.CashflowService;
import com.store.catalog.Product;
import com.store.catalog.StockService;
import com.store.order.Order;
import com.store.order.OrderItem;
import com.store.order.OrderService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReturnService {
    private final OrderService orderService;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private int returnSequence = 0;

    public ReturnService(OrderService orderService, StockService stockService, CashflowService cashflowService) {
        this.orderService = orderService;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public ReturnRequest createReturnRequest(String orderId) {
        Order order = orderService.findById(orderId);
        if (!order.isPaid()) {
            throw new IllegalStateException("Only paid orders can be returned: " + orderId);
        }
        returnSequence++;
        String returnId = "RET" + returnSequence;
        ReturnRequest request = new ReturnRequest(returnId, orderId);
        returns.put(returnId, request);
        return request;
    }

    public void addReturnedItem(String returnId, String productId, int quantity) {
        ReturnRequest request = findById(returnId);
        Order order = orderService.findById(request.getOrderId());
        int alreadyReturned = 0;
        for (ReturnItem item : request.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                alreadyReturned += item.getQuantity();
            }
        }
        int purchasedQuantity = order.getQuantityOf(productId);
        if (purchasedQuantity == 0) {
            throw new IllegalArgumentException("Product " + productId + " was not part of order " + request.getOrderId());
        }
        if (alreadyReturned + quantity > purchasedQuantity) {
            throw new IllegalStateException("Returned quantity exceeds purchased quantity for product " + productId);
        }
        Product product = findProductFromOrder(order, productId);
        request.addItem(new ReturnItem(product, quantity));
    }

    private Product findProductFromOrder(Order order, String productId) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getProduct();
            }
        }
        throw new IllegalArgumentException("Product not found in order: " + productId);
    }

    public double calculateRefundTotal(String returnId) {
        ReturnRequest request = findById(returnId);
        Order order = orderService.findById(request.getOrderId());
        double total = 0.0;
        for (ReturnItem item : request.getItems()) {
            double unitPrice = findUnitPrice(order, item.getProduct().getId());
            total += item.getRefundAmount(unitPrice);
        }
        return total;
    }

    private double findUnitPrice(Order order, String productId) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getUnitPrice();
            }
        }
        throw new IllegalArgumentException("Product not found in order: " + productId);
    }

    public void processRefund(String returnId) {
        ReturnRequest request = findById(returnId);
        if (!request.hasItems()) {
            throw new IllegalStateException("A return must contain at least one returned item: " + returnId);
        }
        if (request.isProcessed()) {
            throw new IllegalStateException("Return request already processed: " + returnId);
        }
        double refundTotal = calculateRefundTotal(returnId);
        for (ReturnItem item : request.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        request.markProcessed(refundTotal);
        cashflowService.recordOutflow(refundTotal, "Refund for return " + returnId);
    }

    public ReturnRequest findById(String returnId) {
        ReturnRequest request = returns.get(returnId);
        if (request == null) {
            throw new IllegalArgumentException("Return request not found: " + returnId);
        }
        return request;
    }

    public List<ReturnRequest> all() {
        return new ArrayList<>(returns.values());
    }
}