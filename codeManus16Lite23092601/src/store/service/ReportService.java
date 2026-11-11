package store.service;

import java.util.LinkedHashMap;
import java.util.Map;
import store.model.Models.OrderStatus;
import store.model.Transactions.CashflowEntry;
import store.model.Transactions.OrderItem;
import store.model.Transactions.ReturnRequest;
import store.repository.DataStore;

public final class ReportService {
    private final DataStore db;
    public ReportService(DataStore db) { this.db = db; }
    public String stockReport() { StringBuilder s = new StringBuilder("STOCK REPORT\n"); db.products.values().forEach(p -> s.append(p.getId()).append("=").append(p.getStock()).append("\n")); return s.toString(); }
    public String salesReport() { Map<String,Integer> sales = new LinkedHashMap<>(); db.products.keySet().forEach(k -> sales.put(k, 0)); db.orders.values().stream().filter(o -> o.getStatus() != OrderStatus.CANCELLED && o.getStatus() != OrderStatus.CREATED).forEach(o -> o.getItems().forEach(i -> sales.computeIfPresent(i.getProduct().getId(), (k,v) -> v + i.getQuantity()))); StringBuilder s = new StringBuilder("SALES BY PRODUCT REPORT\n"); sales.forEach((k,v) -> s.append(k).append("=").append(v).append("\n")); return s.toString(); }
    public String ordersReport() { Map<OrderStatus,Integer> counts = new LinkedHashMap<>(); for (OrderStatus status : OrderStatus.values()) counts.put(status, 0); db.orders.values().forEach(o -> counts.compute(o.getStatus(), (k,v) -> v + 1)); StringBuilder s = new StringBuilder("ORDERS BY STATUS REPORT\n"); counts.forEach((k,v) -> s.append(k).append("=").append(v).append("\n")); return s.toString(); }
    public String cashflowReport() { double in = db.cashflow.stream().filter(e -> e.getType().name().equals("INFLOW")).mapToDouble(CashflowEntry::getAmount).sum(); double out = db.cashflow.stream().filter(e -> e.getType().name().equals("OUTFLOW")).mapToDouble(CashflowEntry::getAmount).sum(); return String.format("CASHFLOW SUMMARY REPORT%nINFLOW=%.2f%nOUTFLOW=%.2f%nNET=%.2f%n", in, out, in - out); }
    public String returnsReport() { StringBuilder s = new StringBuilder("RETURNS/REFUNDS REPORT\n"); for (ReturnRequest r : db.returns.values()) s.append(r.getId()).append("=").append(String.format("%.2f", r.getRefundTotal())).append("\n"); return s.toString(); }
    public String restockingReport() { StringBuilder s = new StringBuilder("RESTOCKING REPORT\n"); db.restocks.values().forEach(r -> s.append(r.getId()).append(" product=").append(r.getProduct().getId()).append(" supplier=").append(r.getSupplier().getId()).append(" quantity=").append(r.getQuantity()).append(" total=").append(String.format("%.2f", r.getTotalCost())).append("\n")); return s.toString(); }
}
