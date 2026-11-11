package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EmptyReturnException;
import com.store.exception.EntityNotFoundException;
import com.store.exception.InvalidOrderStateException;
import com.store.exception.InvalidQuantityException;
import com.store.exception.ReturnQuantityExceededException;
import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.model.OrderStatus;
import com.store.model.Product;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.ReturnRequestRepository;

public class ReturnService {
    private final OrderRepository orderRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;

    public ReturnService(OrderRepository orderRepository, ReturnRequestRepository returnRequestRepository,
                          ProductRepository productRepository, StockService stockService, CashflowService cashflowService) {
        this.orderRepository = orderRepository;
        this.returnRequestRepository = returnRequestRepository;
        this.productRepository = productRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public ReturnRequest createReturnRequest(String returnId, String orderId) {
        if (returnRequestRepository.existsById(returnId)) {
            throw new DuplicateIdException("Duplicate return id: " + returnId);
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Only paid orders can be returned: " + orderId);
        }
        ReturnRequest returnRequest = new ReturnRequest(returnId, orderId);
        returnRequestRepository.save(returnRequest);
        return returnRequest;
    }

    public void addReturnItem(String returnId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Returned quantity must be positive for product " + productId);
        }
        ReturnRequest returnRequest = findReturnRequest(returnId);
        Order order = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + returnRequest.getOrderId()));
        OrderItem orderItem = order.findItemByProductId(productId);
        if (orderItem == null) {
            throw new EntityNotFoundException("Product " + productId + " was not part of order " + order.getId());
        }
        int alreadyRequested = 0;
        for (ReturnItem item : returnRequest.getItems()) {
            if (item.getProductId().equals(productId)) {
                alreadyRequested += item.getQuantity();
            }
        }
        int remaining = orderItem.getRemainingReturnable() - alreadyRequested;
        if (quantity > remaining) {
            throw new ReturnQuantityExceededException("Returned quantity exceeds quantity originally bought for product " + productId);
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
        returnRequest.addItem(new ReturnItem(productId, product.getName(), quantity, orderItem.getUnitPrice()));
    }

    public double calculateRefundTotal(String returnId) {
        ReturnRequest returnRequest = findReturnRequest(returnId);
        double total = 0.0;
        for (ReturnItem item : returnRequest.getItems()) {
            total += item.getSubtotal();
        }
        return total;
    }

    public double processRefund(String returnId) {
        ReturnRequest returnRequest = findReturnRequest(returnId);
        if (returnRequest.isProcessed()) {
            throw new InvalidOrderStateException("Return request already processed: " + returnId);
        }
        if (returnRequest.getItems().isEmpty()) {
            throw new EmptyReturnException("Return request must contain at least one item: " + returnId);
        }
        Order order = orderRepository.findById(returnRequest.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + returnRequest.getOrderId()));
        double total = 0.0;
        for (ReturnItem item : returnRequest.getItems()) {
            stockService.increaseStock(item.getProductId(), item.getQuantity());
            OrderItem orderItem = order.findItemByProductId(item.getProductId());
            orderItem.markReturned(item.getQuantity());
            total += item.getSubtotal();
        }
        returnRequest.markProcessed(total);
        cashflowService.addOutflow(total, "Refund for return " + returnId);
        return total;
    }

    public ReturnRequest findReturnRequest(String returnId) {
        return returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new EntityNotFoundException("Return request not found: " + returnId));
    }
}