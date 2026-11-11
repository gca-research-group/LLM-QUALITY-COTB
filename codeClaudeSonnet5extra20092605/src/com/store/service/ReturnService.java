package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Order;
import com.store.model.OrderStatus;
import com.store.model.Product;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReturnService {
    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    private final OrderService orderService;
    private final StockService stockService;
    private final CashflowService cashflowService;

    public ReturnService(OrderService orderService, StockService stockService, CashflowService cashflowService) {
        this.orderService = orderService;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public ReturnRequest createReturnRequest(String returnId, String orderId) {
        if (returns.containsKey(returnId)) {
            throw new BusinessRuleException("Duplicate return identifier: " + returnId);
        }
        Order order = orderService.findById(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessRuleException("Only paid orders can be returned: " + orderId);
        }
        ReturnRequest returnRequest = new ReturnRequest(returnId, order);
        returns.put(returnId, returnRequest);
        return returnRequest;
    }

    public void addReturnedItem(String returnId, Product product, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Returned quantity must be positive");
        }
        ReturnRequest returnRequest = findById(returnId);
        Order order = returnRequest.getOrder();
        int alreadyReturned = 0;
        for (ReturnItem item : returnRequest.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                alreadyReturned += item.getQuantity();
            }
        }
        int purchasedQuantity = order.getPurchasedQuantity(product.getId());
        if (alreadyReturned + quantity > purchasedQuantity) {
            throw new BusinessRuleException("Returned quantity exceeds purchased quantity for product " + product.getId());
        }
        returnRequest.addItem(new ReturnItem(product, quantity));
    }

    public ReturnRequest findById(String returnId) {
        ReturnRequest returnRequest = returns.get(returnId);
        if (returnRequest == null) {
            throw new BusinessRuleException("Return request not found: " + returnId);
        }
        return returnRequest;
    }

    public double calculateRefundTotal(String returnId) {
        ReturnRequest returnRequest = findById(returnId);
        double total = 0.0;
        for (ReturnItem item : returnRequest.getItems()) {
            total += item.getRefundAmount(item.getProduct().getUnitPrice());
        }
        return total;
    }

    public double processRefund(String refundEntryId, String returnId) {
        ReturnRequest returnRequest = findById(returnId);
        if (returnRequest.getItems().isEmpty()) {
            throw new BusinessRuleException("A return must contain at least one returned item: " + returnId);
        }
        double refundTotal = calculateRefundTotal(returnId);
        for (ReturnItem item : returnRequest.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        returnRequest.markProcessed(refundTotal);
        cashflowService.recordOutflow(refundEntryId, refundTotal, "Refund for return " + returnId);
        return refundTotal;
    }

    public List<ReturnRequest> allReturns() {
        return new ArrayList<>(returns.values());
    }
}