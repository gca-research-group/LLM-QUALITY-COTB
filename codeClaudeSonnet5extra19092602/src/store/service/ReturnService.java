package store.service;

import store.exception.BusinessRuleException;
import store.model.Order;
import store.model.OrderItem;
import store.model.Product;
import store.model.ReturnItem;
import store.model.ReturnRequest;
import store.repository.ReturnRequestRepository;

import java.util.List;

public class ReturnService {
    private final ReturnRequestRepository returnRequestRepository;
    private final OrderService orderService;
    private final CatalogService catalogService;
    private final CashflowService cashflowService;
    private int returnCounter;

    public ReturnService(ReturnRequestRepository returnRequestRepository, OrderService orderService,
                          CatalogService catalogService, CashflowService cashflowService) {
        this.returnRequestRepository = returnRequestRepository;
        this.orderService = orderService;
        this.catalogService = catalogService;
        this.cashflowService = cashflowService;
        this.returnCounter = 0;
    }

    public ReturnRequest createReturnRequest(String orderId) {
        Order order = orderService.findOrder(orderId);
        returnCounter++;
        String returnId = "RET" + returnCounter;
        ReturnRequest returnRequest = new ReturnRequest(returnId, order);
        returnRequestRepository.add(returnRequest);
        return returnRequest;
    }

    public ReturnRequest findReturnRequest(String returnId) {
        ReturnRequest returnRequest = returnRequestRepository.findById(returnId);
        if (returnRequest == null) {
            throw new BusinessRuleException("Return request not found: " + returnId);
        }
        return returnRequest;
    }

    public void addReturnedItem(String returnId, String productId, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Returned quantity must be positive for product " + productId);
        }
        ReturnRequest returnRequest = findReturnRequest(returnId);
        Product product = catalogService.findProduct(productId);
        double unitPrice = findUnitPriceFromOrder(returnRequest.getOrder(), productId);
        returnRequest.addItem(new ReturnItem(product, quantity, unitPrice));
    }

    private double findUnitPriceFromOrder(Order order, String productId) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                return item.getUnitPrice();
            }
        }
        throw new BusinessRuleException("Product " + productId + " was not part of order " + order.getId());
    }

    public double calculateRefundTotal(String returnId) {
        return findReturnRequest(returnId).calculateRefundTotal();
    }

    public double processRefund(String returnId) {
        ReturnRequest returnRequest = findReturnRequest(returnId);
        double refundTotal = returnRequest.calculateRefundTotal();
        returnRequest.markProcessed(refundTotal);
        for (ReturnItem item : returnRequest.getItems()) {
            catalogService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        cashflowService.recordOutflow(refundTotal, "Refund for return " + returnId);
        return refundTotal;
    }

    public List<ReturnRequest> findAllReturns() {
        return returnRequestRepository.findAll();
    }
}