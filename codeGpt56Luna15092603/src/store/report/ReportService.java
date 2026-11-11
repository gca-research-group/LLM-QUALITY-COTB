package store.report;

import store.model.CashflowEntry;
import store.model.Order;
import store.model.Product;
import store.model.Restock;
import store.model.ReturnRequest;
import store.repository.StoreRepository;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ReportService {
    private final StoreRepository repository;

    public ReportService(StoreRepository repository) {
        this.repository = repository;
    }

    public String stockReport() {
        StringBuilder result = new StringBuilder("STOCK REPORT\n");

        for (Product product : repository.getCatalog().products()) {
            result.append(product.getId())
                    .append(" = ")
                    .append(repository.stock(product.getId()))
                    .append('\n');
        }

        return result.toString();
    }

    public String salesByProduct() {
        Map<String, Integer> sales = new LinkedHashMap<>();

        for (Product product : repository.getCatalog().products()) {
            sales.put(product.getId(), 0);
        }

        for (Order order : repository.orders()) {
            if (order.getStatus() == Order.Status.CANCELLED) {
                continue;
            }

            for (Order.OrderItem item : order.getItems()) {
                sales.put(
                        item.getProductId(),
                        sales.getOrDefault(item.getProductId(), 0) + item.getQuantity()
                );
            }
        }

        StringBuilder result = new StringBuilder("SALES BY PRODUCT REPORT\n");

        sales.forEach((productId, quantity) ->
                result.append(productId)
                        .append(" = ")
                        .append(quantity)
                        .append('\n')
        );

        return result.toString();
    }

    public String ordersByStatus() {
        Map<Order.Status, Integer> counts = new EnumMap<>(Order.Status.class);

        for (Order.Status status : Order.Status.values()) {
            counts.put(status, 0);
        }

        for (Order order : repository.orders()) {
            counts.put(
                    order.getStatus(),
                    counts.get(order.getStatus()) + 1
            );
        }

        StringBuilder result = new StringBuilder("ORDERS BY STATUS REPORT\n");

        counts.forEach((status, count) ->
                result.append(status)
                        .append(" = ")
                        .append(count)
                        .append('\n')
        );

        return result.toString();
    }

    public String cashflowSummary() {
        double inflow = 0;
        double outflow = 0;

        for (CashflowEntry entry : repository.cashflows()) {
            if (entry.getType() == CashflowEntry.Type.INFLOW) {
                inflow += entry.getAmount();
            } else {
                outflow += entry.getAmount();
            }
        }

        return String.format(
                Locale.US,
                "CASHFLOW SUMMARY REPORT%nTotal inflow = %.2f%nTotal outflow = %.2f%nNet cashflow = %.2f%n",
                inflow,
                outflow,
                inflow - outflow
        );
    }

    public String returnsAndRefunds() {
        StringBuilder result = new StringBuilder("RETURNS/REFUNDS REPORT\n");

        for (ReturnRequest returnRequest : repository.returns()) {
            result.append(returnRequest.getId())
                    .append(" = ")
                    .append(String.format(Locale.US, "%.2f", returnRequest.refundTotal()))
                    .append('\n');
        }

        return result.toString();
    }

    public String restocking() {
        StringBuilder result = new StringBuilder("RESTOCKING REPORT\n");

        for (Restock restock : repository.restocks()) {
            result.append(restock.getId())
                    .append(" product=")
                    .append(restock.getProductId())
                    .append(" supplier=")
                    .append(restock.getSupplierId())
                    .append(" quantity=")
                    .append(restock.getQuantity())
                    .append(" totalCost=")
                    .append(String.format(Locale.US, "%.2f", restock.totalCost()))
                    .append('\n');
        }

        return result.toString();
    }
}