package com.store.report;

import com.store.cashflow.CashflowService;
import com.store.catalog.Catalog;
import com.store.catalog.Product;
import com.store.order.Order;
import com.store.order.OrderItem;
import com.store.order.OrderService;
import com.store.order.OrderStatus;
import com.store.restock.RestockEntry;
import com.store.restock.RestockService;
import com.store.returns.ReturnItem;
import com.store.returns.ReturnRequest;
import com.store.returns.ReturnService;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final Catalog catalog;
    private final OrderService orderService;
    private final CashflowService cashflowService;
    private final ReturnService returnService;
    private final RestockService restockService;

    public ReportService(Catalog catalog, OrderService orderService, CashflowService cashflowService,
                          ReturnService returnService, RestockService restockService) {
        this.catalog = catalog;
        this.orderService = orderService;
        this.cashflowService = cashflowService;
        this.returnService = returnService;
        this.restockService = restockService;
    }

    public void printStockReport() {
        System.out.println("--- Stock Report ---");
        for (Product product : catalog.allProducts()) {
            System.out.println("Product " + product.getId() + " (" + product.getName() + "): stock = " + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        System.out.println("--- Sales By Product Report ---");
        Map<String, Integer> soldQuantities = new LinkedHashMap<>();
        for (Product product : catalog.allProducts()) {
            soldQuantities.put(product.getId(), 0);
        }
        for (Order order : orderService.all()) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            for (OrderItem item : order.getItems()) {
                String productId = item.getProduct().getId();
                soldQuantities.put(productId, soldQuantities.getOrDefault(productId, 0) + item.getQuantity());
            }
        }
        for (Map.Entry<String, Integer> entry : soldQuantities.entrySet()) {
            System.out.println("Product " + entry.getKey() + ": sold quantity = " + entry.getValue());
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("--- Orders By Status Report ---");
        Map<OrderStatus, Integer> counts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderService.all()) {
            counts.put(order.getStatus(), counts.get(order.getStatus()) + 1);
        }
        for (Map.Entry<OrderStatus, Integer> entry : counts.entrySet()) {
            System.out.println("Status " + entry.getKey() + ": count = " + entry.getValue());
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
        for (ReturnRequest request : returnService.all()) {
            System.out.println("Return " + request.getId() + " (order " + request.getOrderId() + "): refund total = " + request.getRefundTotal());
            for (ReturnItem item : request.getItems()) {
                System.out.println("  Product " + item.getProduct().getId() + ": returned quantity = " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        System.out.println("--- Restocking Report ---");
        for (RestockEntry entry : restockService.all()) {
            System.out.println("Restock " + entry.getId() + ": product = " + entry.getProductId()
                    + ", supplier = " + entry.getSupplierId() + ", quantity = " + entry.getQuantity()
                    + ", total cost = " + entry.getTotalCost());
        }
    }
}