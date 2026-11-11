package store.report;

import store.model.*;
import store.repository.StoreRepository;
import java.util.*;

public class ReportService {
    private final StoreRepository repository;

    public ReportService(StoreRepository repository) {
        this.repository = repository;
    }

    public String stockReport() {
        StringBuilder builder = new StringBuilder("STOCK REPORT\n");

        for (var entry : repository.stock().entrySet()) {
            builder.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append('\n');
        }

        return builder.toString();
    }

    public String salesByProduct() {
        Map<String, Integer> sales = new LinkedHashMap<>();

        for (Product product : repository.products().values()) {
            sales.put(product.getId(), 0);
        }

        for (Order order : repository.orders().values()) {
            if (order.getStatus() != Order.Status.CANCELLED) {
                for (Order.Item item : order.getItems()) {
                    sales.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
                }
            }
        }

        for (ReturnRequest request : repository.returns().values()) {
            for (var entry : request.getItems().entrySet()) {
                sales.merge(entry.getKey(), -entry.getValue(), Integer::sum);
            }
        }

        StringBuilder builder = new StringBuilder("SALES BY PRODUCT\n");

        sales.forEach((id, quantity) ->
                builder.append(id).append(" = ").append(quantity).append('\n'));

        return builder.toString();
    }

    public String ordersByStatus() {
        Map<Order.Status, Integer> counts = new EnumMap<>(Order.Status.class);

        for (Order.Status status : Order.Status.values()) {
            counts.put(status, 0);
        }

        for (Order order : repository.orders().values()) {
            counts.merge(order.getStatus(), 1, Integer::sum);
        }

        StringBuilder builder = new StringBuilder("ORDERS BY STATUS\n");

        counts.forEach((status, count) ->
                builder.append(status).append(" = ").append(count).append('\n'));

        return builder.toString();
    }

    public String cashflow(double inflow, double outflow) {
        return String.format(
                Locale.US,
                "CASHFLOW SUMMARY%nTotal inflow = %.2f%nTotal outflow = %.2f%nNet cashflow = %.2f%n",
                inflow,
                outflow,
                inflow - outflow
        );
    }

    public String returns() {
        StringBuilder builder = new StringBuilder("RETURNS/REFUNDS REPORT\n");

        for (ReturnRequest request : repository.returns().values()) {
            builder.append(request.getId())
                    .append(" = ")
                    .append(String.format(Locale.US, "%.2f", request.getRefundTotal()))
                    .append('\n');
        }

        return builder.toString();
    }

    public String restocks() {
        StringBuilder builder = new StringBuilder("RESTOCKING REPORT\n");

        for (Restock restock : repository.restocks().values()) {
            builder.append(restock.getId())
                    .append(" product=").append(restock.getProduct().getId())
                    .append(" supplier=").append(restock.getSupplier().getId())
                    .append(" quantity=").append(restock.getQuantity())
                    .append(" totalCost=")
                    .append(String.format(Locale.US, "%.2f", restock.totalCost()))
                    .append('\n');
        }

        return builder.toString();
    }
}