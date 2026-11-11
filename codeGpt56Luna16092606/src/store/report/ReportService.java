package store.report;

import java.util.LinkedHashMap;
import java.util.Map;
import store.model.CashflowEntry;
import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Product;
import store.model.ReturnItem;
import store.model.ReturnRequest;
import store.model.RestockEntry;
import store.service.CashflowService;

public class ReportService {
    public void printStockReport(java.util.Collection<Product> products) {
        System.out.println("STOCK REPORT");

        for (Product product : products) {
            System.out.println(
                product.getId() + "=" + product.getStock()
            );
        }
    }

    public void printSalesByProductReport(
        java.util.Collection<Order> orders,
        java.util.Collection<Product> products
    ) {
        Map<String, Integer> sold = new LinkedHashMap<String, Integer>();

        for (Product product : products) {
            sold.put(product.getId(), Integer.valueOf(0));
        }

        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    sold.put(
                        item.getProduct().getId(),
                        Integer.valueOf(
                            sold.get(item.getProduct().getId()).intValue()
                                + item.getQuantity()
                        )
                    );
                }
            }
        }

        System.out.println("SALES BY PRODUCT REPORT");

        for (Product product : products) {
            System.out.println(
                product.getId() + "=" + sold.get(product.getId())
            );
        }
    }

    public void printOrdersByStatusReport(
        java.util.Collection<Order> orders
    ) {
        Map<OrderStatus, Integer> counts =
            new LinkedHashMap<OrderStatus, Integer>();

        for (OrderStatus status : OrderStatus.values()) {
            counts.put(status, Integer.valueOf(0));
        }

        for (Order order : orders) {
            counts.put(
                order.getStatus(),
                Integer.valueOf(
                    counts.get(order.getStatus()).intValue() + 1
                )
            );
        }

        System.out.println("ORDERS BY STATUS REPORT");

        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(
                status + "=" + counts.get(status)
            );
        }
    }

    public void printCashflowSummary(CashflowService cashflow) {
        System.out.println("CASHFLOW SUMMARY REPORT");
        System.out.printf(
            "TOTAL INFLOW=%.2f%n",
            cashflow.totalInflow()
        );
        System.out.printf(
            "TOTAL OUTFLOW=%.2f%n",
            cashflow.totalOutflow()
        );
        System.out.printf(
            "NET CASHFLOW=%.2f%n",
            cashflow.net()
        );
    }

    public void printReturnsReport(
        java.util.Collection<ReturnRequest> returns
    ) {
        System.out.println("RETURNS/REFUNDS REPORT");

        for (ReturnRequest request : returns) {
            System.out.printf(
                "%s=%.2f",
                request.getId(),
                request.getRefundTotal()
            );

            for (ReturnItem item : request.getItems()) {
                System.out.print(
                    " " + item.getProduct().getId()
                        + "x" + item.getQuantity()
                );
            }

            System.out.println();
        }
    }

    public void printRestockingReport(
        java.util.Collection<RestockEntry> entries
    ) {
        System.out.println("RESTOCKING REPORT");

        for (RestockEntry entry : entries) {
            System.out.printf(
                "%s product=%s supplier=%s quantity=%d total=%.2f%n",
                entry.getId(),
                entry.getProduct().getId(),
                entry.getSupplier().getId(),
                entry.getQuantity(),
                entry.getTotalCost()
            );
        }
    }

    public void printCashflowEntries(
        java.util.Collection<CashflowEntry> entries
    ) {
        for (CashflowEntry entry : entries) {
            System.out.printf(
                "%s %s %.2f %s%n",
                entry.getId(),
                entry.getType(),
                entry.getAmount(),
                entry.getDescription()
            );
        }
    }
}