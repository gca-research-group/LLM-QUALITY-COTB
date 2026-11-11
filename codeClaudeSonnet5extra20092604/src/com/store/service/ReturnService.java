package com.store.service;

import com.store.exception.StoreOperationException;
import com.store.model.Product;
import com.store.order.Order;
import com.store.order.OrderStatus;
import com.store.repository.ReturnRequestRepository;
import com.store.returns.ReturnItem;
import com.store.returns.ReturnRequest;

import java.util.List;

public class ReturnService {
    private final ReturnRequestRepository returnRequestRepository;
    private final OrderService orderService;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private final CatalogService catalogService;

    public ReturnService(ReturnRequestRepository returnRequestRepository, OrderService orderService,
                          StockService stockService, CashflowService cashflowService, CatalogService catalogService) {
        this.returnRequestRepository = returnRequestRepository;
        this.orderService = orderService;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
        this.catalogService = catalogService;
    }

    public ReturnRequest createReturnRequest(String returnId, String orderId) {
        Order order = orderService.getOrderOrThrow(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new StoreOperationException("Only paid orders can be returned: " + orderId);
        }
        ReturnRequest request = new ReturnRequest(returnId, orderId);
        returnRequestRepository.add(request);
        return request;
    }

    public ReturnRequest getReturnOrThrow(String returnId) {
        return returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new StoreOperationException("Return request not found: " + returnId));
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new StoreOperationException("Returned quantity must be positive");
        }
        ReturnRequest request = getReturnOrThrow(returnId);
        if (request.isProcessed()) {
            throw new StoreOperationException("Cannot add items to a processed return request: " + returnId);
        }
        Order order = orderService.getOrderOrThrow(request.getOrderId());
        int purchasedQty = order.getPurchasedQuantity(productId);
        int alreadyReturned = alreadyReturnedQuantity(request.getOrderId(), productId);
        if (alreadyReturned + quantity > purchasedQty) {
            throw new StoreOperationException("Returned quantity exceeds quantity originally bought for product " + productId);
        }
        double unitPrice = order.getUnitPriceForProduct(productId);
        Product product = catalogService.getProductOrThrow(productId);
        request.addItem(new ReturnItem(product, quantity, unitPrice));
    }

    private int alreadyReturnedQuantity(String orderId, String productId) {
        int total = 0;
        for (ReturnRequest request : returnRequestRepository.findAll()) {
            if (request.getOrderId().equals(orderId)) {
                for (ReturnItem item : request.getItems()) {
                    if (item.getProduct().getId().equals(productId)) {
                        total += item.getQuantity();
                    }
                }
            }
        }
        return total;
    }

    public double processRefund(String returnId) {
        ReturnRequest request = getReturnOrThrow(returnId);
        if (!request.hasItems()) {
            throw new StoreOperationException("A return must contain at least one returned item: " + returnId);
        }
        if (request.isProcessed()) {
            throw new StoreOperationException("Return already processed: " + returnId);
        }
        double refundTotal = request.getRefundTotal();
        for (ReturnItem item : request.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        cashflowService.recordOutflow("Refund for return " + returnId, refundTotal);
        request.markProcessed();
        return refundTotal;
    }

    public List<ReturnRequest> listAll() {
        return returnRequestRepository.findAll();
    }
}