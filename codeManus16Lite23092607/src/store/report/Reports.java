package store.report;

import store.domain.Domain;
import store.service.StoreService;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Reports {
    private Reports() {}
    public static String stock(StoreService s) { StringBuilder b = new StringBuilder("STOCK REPORT\n"); for (Domain.Product p : s.products().values()) b.append(p.getId()).append("=").append(p.getStock()).append("\n"); return b.toString(); }
    public static String salesByProduct(StoreService s) { Map<String,Integer> totals = new LinkedHashMap<>(); for (Domain.Product p : s.products().values()) totals.put(p.getId(), 0); for (Domain.Order o : s.orders().values()) if (o.getStatus() != Domain.OrderStatus.CANCELLED) for (Domain.OrderItem i : o.getItems()) totals.put(i.getProduct().getId(), totals.get(i.getProduct().getId()) + i.getQuantity()); StringBuilder b = new StringBuilder("SALES BY PRODUCT REPORT\n"); totals.forEach((k,v) -> b.append(k).append("=").append(v).append("\n")); return b.toString(); }
    public static String ordersByStatus(StoreService s) { Map<Domain.OrderStatus,Integer> counts = new EnumMap<>(Domain.OrderStatus.class); for (Domain.OrderStatus st : Domain.OrderStatus.values()) counts.put(st, 0); for (Domain.Order o : s.orders().values()) counts.put(o.getStatus(), counts.get(o.getStatus()) + 1); return "ORDERS BY STATUS REPORT\n" + counts + "\n"; }
    public static String cashflow(StoreService s) { return String.format("CASHFLOW SUMMARY REPORT%nIN=%.2f OUT=%.2f NET=%.2f%n", s.totalInflow(), s.totalOutflow(), s.totalInflow() - s.totalOutflow()); }
    public static String returns(StoreService s) { StringBuilder b = new StringBuilder("RETURNS/REFUNDS REPORT\n"); for (Domain.ReturnRequest r : s.returns().values()) b.append(r.getId()).append(" refund=").append(String.format("%.2f", r.total())).append("\n"); return b.toString(); }
    public static String restocking(StoreService s) { StringBuilder b = new StringBuilder("RESTOCKING REPORT\n"); for (Domain.Restock r : s.restocks().values()) b.append(r.getId()).append(" product=").append(r.getProduct().getId()).append(" supplier=").append(r.getSupplier().getId()).append(" quantity=").append(r.getQuantity()).append(" total=").append(String.format("%.2f", r.totalCost())).append("\n"); return b.toString(); }
}
