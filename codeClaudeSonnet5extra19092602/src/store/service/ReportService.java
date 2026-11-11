package store.service;

import store.model.OrderStatus;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnItem;
import store.model.ReturnRequest;

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
        for (Product product : catalogService.listAllProducts()) {
            System.out.println(product.getId() + " - " + product.getName() + " - stock: " + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        System.out.println("--- Sales By Product Report ---");
        Map<String, Integer> sales = orderService.salesByProduct();
        for (Product product : catalogService.listAllProducts()) {
            int sold = sales.getOrDefault(product.getId(), 0);
            System.out.println(product.getId() + " - " + product.getName() + " - sold quantity: " + sold);
        }
    }

    public void printOrdersByStatusReport() {
        System.out.println("--- Orders By Status Report ---");
        Map<OrderStatus, Integer> counts = orderService.ordersByStatus();
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(status + ": " + counts.getOrDefault(status, 0));
        }
    }

    public void printCashflowSummaryReport() {
        System.out.println("--- Cashflow Summary Report ---");
        System.out.println("Total inflow: " + cashflowService.totalInflow());
        System.out.println("Total outflow: " + cashflowService.totalOutflow());
        System.out.println("Net cashflow: " + cashflowService.netCashflow());
    }

    public void printReturnsRefundsReport() {
        System.out.println("--- Returns/Refunds Report ---");
        for (ReturnRequest returnRequest : returnService.findAllReturns()) {
            System.out.println("Return " + returnRequest.getId() + " for order " + returnRequest.getOrder().getId()
                    + " - refund total: " + returnRequest.getRefundTotal());
            for (ReturnItem item : returnRequest.getItems()) {
                System.out.println("  product: " + item.getProduct().getId() + " quantity: " + item.getQuantity());
            }
        }
    }

    public void printRestockingReport() {
        System.out.println("--- Restocking Report ---");
        for (RestockEntry entry : restockService.findAllRestocks()) {
            System.out.println("Restock " + entry.getId() + " - product: " + entry.getProduct().getId()
                    + " - supplier: " + entry.getSupplier().getId() + " - quantity: " + entry.getQuantity()
                    + " - total cost: " + entry.getTotalCost());
        }
    }
}