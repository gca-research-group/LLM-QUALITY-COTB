package store.service;

import store.exception.InvalidOperationException;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Product;
import store.model.ReturnItem;
import store.model.ReturnRequest;
import store.repository.ReturnRequestRepository;

import java.util.ArrayList;
import java.util.List;

public class ReturnService {
    private final ReturnRequestRepository returnRequestRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private int counter;

    public ReturnService(ReturnRequestRepository returnRequestRepository, StockService stockService, CashflowService cashflowService) {
        this.returnRequestRepository = returnRequestRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
        this.counter = 0;
    }

    public ReturnRequest createReturnRequest(Order order) {
        counter++;
        String id = "RET" + counter;
        ReturnRequest request = new ReturnRequest(id, order);
        returnRequestRepository.add(request);
        return request;
    }

    public void addReturnedItem(ReturnRequest request, Product product, int quantity) {
        ReturnItem item = new ReturnItem(product, quantity);
        request.addItem(item);
    }

    public double calculateRefundTotal(ReturnRequest request) {
        double total = 0;
        for (ReturnItem item : request.getItems()) {
            OrderItem orderItem = request.getOrder().findItem(item.getProduct().getId());
            total += item.getQuantity() * orderItem.getUnitPriceAtOrderTime();
        }
        return total;
    }

    public double processRefund(ReturnRequest request) {
        if (request.isProcessed()) {
            throw new InvalidOperationException("Return request already processed: " + request.getId());
        }
        if (request.getItems().isEmpty()) {
            throw new InvalidOperationException("A return must contain at least one returned item: " + request.getId());
        }
        double refundTotal = calculateRefundTotal(request);
        for (ReturnItem item : request.getItems()) {
            OrderItem orderItem = request.getOrder().findItem(item.getProduct().getId());
            orderItem.registerReturn(item.getQuantity());
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        request.markProcessed(refundTotal);
        cashflowService.recordOutflow(refundTotal, "Refund for return " + request.getId());
        if (request.getOrder().getStatus() == OrderStatus.PAID) {
            request.getOrder().markRefunded();
        }
        return refundTotal;
    }

    public List<ReturnRequest> allReturnRequests() {
        return new ArrayList<>(returnRequestRepository.findAll());
    }
}