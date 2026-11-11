package com.store.service;

import java.util.Collection;

import com.store.exception.InvalidOperationException;
import com.store.model.Order;
import com.store.model.OrderStatus;
import com.store.model.Product;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.repository.ReturnRequestRepository;

public class ReturnService {
    private final ReturnRequestRepository returnRequestRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private int sequence = 0;

    public ReturnService(ReturnRequestRepository returnRequestRepository, StockService stockService,
                          CashflowService cashflowService) {
        this.returnRequestRepository = returnRequestRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public ReturnRequest createReturnRequest(Order order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException("Only paid orders can be returned");
        }
        sequence++;
        String id = "RET" + sequence;
        ReturnRequest request = new ReturnRequest(id, order);
        returnRequestRepository.add(request);
        return request;
    }

    public void addReturnedItem(ReturnRequest request, Product product, int quantity) {
        int purchased = request.getOrder().getPurchasedQuantity(product.getId());
        int alreadyReturned = 0;
        for (ReturnItem existing : request.getItems()) {
            if (existing.getProduct().getId().equals(product.getId())) {
                alreadyReturned += existing.getQuantity();
            }
        }
        if (alreadyReturned + quantity > purchased) {
            throw new InvalidOperationException(
                    "Returned quantity exceeds purchased quantity for product " + product.getId());
        }
        double unitPrice = request.getOrder().getUnitPriceForProduct(product.getId());
        ReturnItem item = new ReturnItem(product, quantity, unitPrice);
        request.addItem(item);
    }

    public double processRefund(ReturnRequest request) {
        request.markProcessed();
        double total = request.calculateRefundTotal();
        for (ReturnItem item : request.getItems()) {
            stockService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        cashflowService.registerOutflow(total, "Refund for return " + request.getId());
        return total;
    }

    public Collection<ReturnRequest> findAll() {
        return returnRequestRepository.findAll();
    }
}