package store.report;

import store.model.Model;
import store.service.StoreService;

public class ReportService {
    private final StoreService store;
    public ReportService(StoreService store) { this.store = store; }
    public void printStockReport() { System.out.println("STOCK REPORT"); for (Model.Product p : store.products()) System.out.println(p.getId() + "=" + p.getStock()); }
    public void printSalesByProductReport() { System.out.println("SALES BY PRODUCT REPORT"); for (var e : store.salesByProduct().entrySet()) System.out.println(e.getKey() + "=" + e.getValue()); }
    public void printOrdersByStatusReport() { System.out.println("ORDERS BY STATUS REPORT"); for (var e : store.ordersByStatus().entrySet()) System.out.println(e.getKey() + "=" + e.getValue()); }
    public void printCashflowSummaryReport() { System.out.printf("CASHFLOW SUMMARY REPORT%nINFLOW=%.2f%nOUTFLOW=%.2f%nNET=%.2f%n", store.totalInflow(), store.totalOutflow(), store.totalInflow() - store.totalOutflow()); }
    public void printReturnsReport() { System.out.println("RETURNS/REFUNDS REPORT"); for (Model.ReturnRequest r : store.returns()) System.out.println(r.getId() + " refund=" + String.format("%.2f", r.getRefundTotal()) + " items=" + r.getItems()); }
    public void printRestockingReport() { System.out.println("RESTOCKING REPORT"); for (Model.Restock r : store.restocks()) System.out.println(r.getId() + " product=" + r.getProduct().getId() + " supplier=" + r.getSupplier().getId() + " quantity=" + r.getQuantity() + " total=" + String.format("%.2f", r.totalCost())); }
}
