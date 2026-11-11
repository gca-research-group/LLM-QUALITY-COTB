package com.store.report;

import java.util.LinkedHashMap;
import java.util.Map;

import com.store.model.Order;
import com.store.model.OrderItem;
import com.store.model.OrderStatus;
import com.store.model.Product;
import com.store.model.RestockEntry;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.service.CashflowService;
import com.store.service.CatalogService;
import com.store.service.OrderService;
import com.store.service.RestockService;
import com.store.service.ReturnService;

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
        for (Product product : catalogService.findAllProducts()) {
            System.out.println(product.getId() + " (" + product.getName() + "): " + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        System.out.println("--- Sales By Product Report ---");
        Map<String, Integer> soldByProduct = new LinkedHashMap<>();
        for (Product product : catalogService.findAllProducts()) {
            soldByProduct.put(product.getId(), 0);
        }
        for (Order order : orderService.findAll()) {
            if (order.getStatus() == OrderStatus.CANCELLED) {
                continue;
            }
            for (OrderItem item : order.getItems()) {
                String id = item.getProduct().getId();
                soldByProduct.put(id, soldByProduct.getOrDefault(id, 0) + item.getQuantity());
            }
        }
        for (Map.Entry<String, Integer> entry : soldByProduct.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("--- Orders By Status Report ---");
        Map<OrderStatus, Integer> counts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderService.findAll()) {
            counts.put(order.getStatus(), counts.get(order.getStatus()) + 1);
        }
        for (Map.Entry<OrderStatus, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
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
        for (ReturnRequest request : returnService.findAll()) {
            System.out.println(request.getId() + " - refund total: " + request.calculateRefundTotal());
            for (ReturnItem item : request.getItems()) {
                System.out.println("  product " + item.getProduct().getId() + ": returned quantity " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        System.out.println("--- Restocking Report ---");
        for (RestockEntry entry : restockService.findAll()) {
            System.out.println(entry.getId() + " - product: " + entry.getProduct().getId()
                    + ", supplier: " + entry.getSupplier().getId()
                    + ", quantity: " + entry.getQuantity()
                    + ", total cost: " + entry.getTotalCost());
        }
    }
}