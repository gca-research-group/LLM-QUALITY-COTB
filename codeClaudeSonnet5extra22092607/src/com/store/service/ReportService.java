package com.store.service;

import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.model.OrderStatus;
import com.store.model.Product;
import com.store.model.RestockOperation;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.RestockRepository;
import com.store.repository.ReturnRequestRepository;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CashflowService cashflowService;
    private final ReturnRequestRepository returnRequestRepository;
    private final RestockRepository restockRepository;

    public ReportService(ProductRepository productRepository, OrderRepository orderRepository,
                          CashflowService cashflowService, ReturnRequestRepository returnRequestRepository,
                          RestockRepository restockRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.cashflowService = cashflowService;
        this.returnRequestRepository = returnRequestRepository;
        this.restockRepository = restockRepository;
    }

    public void printStockReport() {
        for (Product product : productRepository.findAll()) {
            System.out.println("Product " + product.getId() + " (" + product.getName() + ") stock: " + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        Map<String, Integer> sold = new LinkedHashMap<>();
        for (Product product : productRepository.findAll()) {
            sold.put(product.getId(), 0);
        }
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.PAID) {
                for (OrderItem item : order.getItems()) {
                    sold.merge(item.getProductId(), item.getQuantity(), Integer::sum);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : sold.entrySet()) {
            System.out.println("Product " + entry.getKey() + " sold quantity: " + entry.getValue());
        }
    }

    public void printOrdersByStatusReport() {
        Map<OrderStatus, Integer> counts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderRepository.findAll()) {
            counts.merge(order.getStatus(), 1, Integer::sum);
        }
        for (Map.Entry<OrderStatus, Integer> entry : counts.entrySet()) {
            System.out.println("Status " + entry.getKey() + " order count: " + entry.getValue());
        }
    }

    public void printCashflowSummaryReport() {
        System.out.println("Total inflow: " + String.format("%.2f", cashflowService.getTotalInflow()));
        System.out.println("Total outflow: " + String.format("%.2f", cashflowService.getTotalOutflow()));
        System.out.println("Net cashflow: " + String.format("%.2f", cashflowService.getNetCashflow()));
    }

    public void printReturnsRefundsReport() {
        for (ReturnRequest returnRequest : returnRequestRepository.findAll()) {
            System.out.println("Return " + returnRequest.getId() + " refund total: "
                    + String.format("%.2f", returnRequest.getRefundTotal()));
            for (ReturnItem item : returnRequest.getItems()) {
                System.out.println("  Returned product " + item.getProductId() + " quantity: " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        for (RestockOperation operation : restockRepository.findAll()) {
            System.out.println("Restock " + operation.getId() + " product: " + operation.getProductId()
                    + " supplier: " + operation.getSupplierId() + " quantity: " + operation.getQuantity()
                    + " total cost: " + String.format("%.2f", operation.getTotalCost()));
        }
    }
}