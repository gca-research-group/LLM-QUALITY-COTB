package store.report;

import store.model.Order;
import store.model.Product;
import store.model.Restock;
import store.model.ReturnRequest;
import store.service.StoreService;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportService {
    private final StoreService store;

    public ReportService(StoreService store) {
        this.store = store;
    }

    public void stockReport() {
        System.out.println("STOCK REPORT");

        for (Product product : store.products()) {
            System.out.printf(
                    "%s = %d%n",
                    product.getId(),
                    store.stock(product.getId())
            );
        }
    }

    public void salesReport() {
        System.out.println("SALES BY PRODUCT REPORT");

        Map<String, Integer> quantities = new LinkedHashMap<>();

        for (Product product : store.products()) {
            quantities.put(product.getId(), 0);
        }

        for (Order order : store.orders()) {
            for (Order.OrderItem item : order.getItems()) {
                if (order.getStatus() != Order.Status.CANCELLED) {
                    quantities.merge(
                            item.getProduct().getId(),
                            item.getQuantity(),
                            Integer::sum
                    );
                }
            }
        }

        for (Map.Entry<String, Integer> entry : quantities.entrySet()) {
            System.out.printf(
                    "%s = %d%n",
                    entry.getKey(),
                    entry.getValue()
            );
        }
    }

    public void ordersStatusReport() {
        System.out.println("ORDERS BY STATUS REPORT");

        for (Order.Status status : Order.Status.values()) {
            long count = store.orders()
                    .stream()
                    .filter(order -> order.getStatus() == status)
                    .count();

            System.out.printf("%s = %d%n", status, count);
        }
    }

    public void cashflowReport() {
        System.out.printf(
                "CASHFLOW SUMMARY: inflow=%.2f outflow=%.2f net=%.2f%n",
                store.cashflow().totalInflow(),
                store.cashflow().totalOutflow(),
                store.cashflow().net()
        );
    }

    public void returnsReport() {
        System.out.println("RETURNS/REFUNDS REPORT");

        for (ReturnRequest request : store.returns()) {
            System.out.printf(
                    "RET%s refund=%.2f%n",
                    request.getId().replace("RET", ""),
                    request.getRefundTotal()
            );
        }
    }

    public void restockingReport() {
        System.out.println("RESTOCKING REPORT");

        for (Restock restock : store.restocks()) {
            System.out.printf(
                    "%s product=%s supplier=%s quantity=%d total=%.2f%n",
                    restock.getId(),
                    restock.getProduct().getId(),
                    restock.getSupplier().getId(),
                    restock.getQuantity(),
                    restock.totalCost()
            );
        }
    }
}