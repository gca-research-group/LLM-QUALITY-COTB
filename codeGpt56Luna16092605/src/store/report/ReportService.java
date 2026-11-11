package store.report;

import java.util.LinkedHashMap;
import java.util.Map;

import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.service.StoreService;

public class ReportService {
    private final StoreService service;

    public ReportService(StoreService service) {
        this.service = service;
    }

    public void printStockReport() {
        System.out.println("STOCK REPORT");

        for (Product product : service.products()) {
            System.out.println(
                    product.getId()
                    + " stock="
                    + product.getStockQuantity());
        }
    }

    public void printSalesByProductReport() {
        Map<String, Integer> sold =
                new LinkedHashMap<String, Integer>();

        for (Product product : service.products()) {
            sold.put(product.getId(), Integer.valueOf(0));
        }

        for (Order order : service.orders()) {
            if (order.getStatus() == OrderStatus.PAID) {
                for (OrderItem item : order.getItems()) {
                    int current =
                            sold.get(item.getProduct().getId()).intValue();

                    sold.put(
                            item.getProduct().getId(),
                            Integer.valueOf(current + item.getQuantity()));
                }
            }
        }

        System.out.println("SALES BY PRODUCT REPORT");

        for (Product product : service.products()) {
            System.out.println(
                    product.getId()
                    + " sold="
                    + sold.get(product.getId()));
        }
    }

    public void printOrdersByStatusReport() {
        Map<OrderStatus, Integer> counts =
                new LinkedHashMap<OrderStatus, Integer>();

        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, Integer.valueOf(0));
        }

        for (Order order : service.orders()) {
            int current =
                    counts.get(order.getStatus()).intValue();

            counts.put(
                    order.getStatus(),
                    Integer.valueOf(current + 1));
        }

        System.out.println("ORDERS BY STATUS REPORT");

        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(
                    status
                    + " count="
                    + counts.get(status));
        }
    }

    public void printCashflowSummaryReport() {
        System.out.println("CASHFLOW SUMMARY REPORT");
        System.out.println("inflow=" + service.totalInflow());
        System.out.println("outflow=" + service.totalOutflow());
        System.out.println("net=" + service.netCashflow());
    }

    public void printReturnsReport() {
        System.out.println("RETURNS/REFUNDS REPORT");

        for (ReturnRequest returnRequest : service.returns()) {
            System.out.println(
                    returnRequest.getId()
                    + " refund="
                    + returnRequest.calculateRefundTotal()
                    + " items="
                    + returnRequest.getQuantities());
        }
    }

    public void printRestockingReport() {
        System.out.println("RESTOCKING REPORT");

        for (RestockEntry entry : service.restocks()) {
            System.out.println(
                    entry.getId()
                    + " product="
                    + entry.getProductId()
                    + " supplier="
                    + entry.getSupplierId()
                    + " quantity="
                    + entry.getQuantity()
                    + " total="
                    + entry.getTotalCost());
        }
    }
}