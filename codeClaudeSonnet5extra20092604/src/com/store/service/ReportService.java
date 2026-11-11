package com.store.service;

import com.store.model.Product;
import com.store.order.Order;
import com.store.order.OrderItem;
import com.store.order.OrderStatus;
import com.store.restock.RestockOperation;
import com.store.returns.ReturnItem;
import com.store.returns.ReturnRequest;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final CatalogService catalogService;
    private final OrderService orderService;
    private final CashflowService cashflowService;
    private final ReturnService returnService;
    private final RestockService restockService;

    public ReportService(CatalogService catalogService, OrderService orderService, CashflowService cashflowService,
                          ReturnService returnService, RestockService restockService) {
        this.catalogService = catalogService;
        this.orderService = orderService;
        this.cashflowService = cashflowService;
        this.returnService = returnService;
        this.restockService = restockService;
    }

    public void printStockReport() {
        System.out.println("--- Stock Report ---");
        for (Product product : catalogService.listAll()) {
            System.out.println(product.getId() + " : " + product.getStock());
        }
    }

    public void printSalesByProductReport() {
        System.out.println("--- Sales By Product Report ---");
        Map<String, Integer> sales = new LinkedHashMap<String, Integer>();
        for (Product product : catalogService.listAll()) {
            sales.put(product.getId(), 0);
        }
        for (Order order : orderService.listAll()) {
            if (order.getStatus() == OrderStatus.PAID) {
                for (OrderItem item : order.getItems()) {
                    String productId = item.getProduct().getId();
                    Integer current = sales.get(productId);
                    int currentValue = current == null ? 0 : current;
                    sales.put(productId, currentValue + item.getQuantity());
                }
            }
        }
        for (Map.Entry<String, Integer> entry : sales.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("--- Orders By Status Report ---");
        Map<OrderStatus, Integer> counts = new LinkedHashMap<OrderStatus, Integer>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderService.listAll()) {
            OrderStatus status = order.getStatus();
            Integer current = counts.get(status);
            int currentValue = current == null ? 0 : current;
            counts.put(status, currentValue + 1);
        }
        for (Map.Entry<OrderStatus, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public void printCashflowSummaryReport() {
        System.out.println("--- Cashflow Summary Report ---");
        System.out.println("Total inflow: " + cashflowService.getTotalInflow());
        System.out.println("Total outflow: " + cashflowService.getTotalOutflow());
        System.out.println("Net cashflow: " + cashflowService.getNetCashflow());
    }

    public void printReturnsRefundsReport() {
        System.out.println("--- Returns/Refunds Report ---");
        for (ReturnRequest request : returnService.listAll()) {
            System.out.println(request.getId() + " (order " + request.getOrderId() + ") : refund total = " + request.getRefundTotal());
            for (ReturnItem item : request.getItems()) {
                System.out.println("    " + item.getProduct().getId() + " : " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        System.out.println("--- Restocking Report ---");
        for (RestockOperation operation : restockService.listAll()) {
            System.out.println(operation.getId() + " : product=" + operation.getProduct().getId()
                    + " supplier=" + operation.getSupplier().getId()
                    + " quantity=" + operation.getQuantity()
                    + " totalCost=" + operation.getTotalCost());
        }
    }
}