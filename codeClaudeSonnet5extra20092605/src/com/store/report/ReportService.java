package com.store.report;

import com.store.model.Order;
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

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final CatalogService catalogService;
    private final OrderService orderService;
    private final CashflowService cashflowService;
    private final ReturnService returnService;
    private final RestockService restockService;

    public ReportService(CatalogService catalogService,
                          OrderService orderService,
                          CashflowService cashflowService,
                          ReturnService returnService,
                          RestockService restockService) {
        this.catalogService = catalogService;
        this.orderService = orderService;
        this.cashflowService = cashflowService;
        this.returnService = returnService;
        this.restockService = restockService;
    }

    public void printStockReport() {
        System.out.println("=== Stock Report ===");
        for (Product product : catalogService.allProducts()) {
            System.out.println(product.getId() + " (" + product.getName() + ") -> stock: " + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        System.out.println("=== Sales By Product Report ===");
        Map<String, Integer> sales = orderService.getSoldQuantities();
        for (Product product : catalogService.allProducts()) {
            int soldQuantity = sales.getOrDefault(product.getId(), 0);
            System.out.println(product.getId() + " (" + product.getName() + ") -> sold quantity: " + soldQuantity);
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("=== Orders By Status Report ===");
        Map<OrderStatus, Integer> counts = new LinkedHashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, 0);
        }
        for (Order order : orderService.allOrders()) {
            counts.merge(order.getStatus(), 1, Integer::sum);
        }
        for (Map.Entry<OrderStatus, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + " -> count: " + entry.getValue());
        }
    }

    public void printCashflowSummaryReport() {
        System.out.println("=== Cashflow Summary Report ===");
        System.out.println("Total inflow: " + cashflowService.totalInflow());
        System.out.println("Total outflow: " + cashflowService.totalOutflow());
        System.out.println("Net cashflow: " + cashflowService.netCashflow());
    }

    public void printReturnsRefundsReport() {
        System.out.println("=== Returns/Refunds Report ===");
        for (ReturnRequest returnRequest : returnService.allReturns()) {
            System.out.println("Return " + returnRequest.getId() + " -> refund total: " + returnRequest.getRefundTotal());
            for (ReturnItem item : returnRequest.getItems()) {
                System.out.println("    product " + item.getProduct().getId() + " -> returned quantity: " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        System.out.println("=== Restocking Report ===");
        for (RestockEntry entry : restockService.allRestocks()) {
            System.out.println("Restock " + entry.getId()
                    + " -> product: " + entry.getProduct().getId()
                    + ", supplier: " + entry.getSupplier().getId()
                    + ", quantity: " + entry.getQuantity()
                    + ", total cost: " + entry.getTotalCost());
        }
    }
}