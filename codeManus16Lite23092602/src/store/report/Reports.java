package store.report;

import store.service.StoreService;
import store.model.StoreTypes.*;
import java.util.*;

public final class Reports {
    private Reports() {}
    public static String stock(StoreService s) { StringBuilder b = new StringBuilder("STOCK REPORT\n"); s.products().forEach(p -> b.append(p.getId()).append("=").append(p.getStock()).append("\n")); return b.toString(); }
    public static String sales(StoreService s) { StringBuilder b = new StringBuilder("SALES BY PRODUCT REPORT\n"); s.salesByProduct().forEach((id, q) -> b.append(id).append("=").append(q).append("\n")); return b.toString(); }
    public static String orders(StoreService s) { StringBuilder b = new StringBuilder("ORDERS BY STATUS REPORT\n"); s.ordersByStatus().forEach((status, count) -> b.append(status).append("=").append(count).append("\n")); return b.toString(); }
    public static String cashflow(StoreService s) { return String.format(Locale.US, "CASHFLOW SUMMARY REPORT%nTOTAL INFLOW=%.2f%nTOTAL OUTFLOW=%.2f%nNET CASHFLOW=%.2f%n", s.totalInflow(), s.totalOutflow(), s.totalInflow() - s.totalOutflow()); }
    public static String returns(StoreService s) { StringBuilder b = new StringBuilder("RETURNS/REFUNDS REPORT\n"); s.returns().forEach(r -> b.append(r.getId()).append(" refund=").append(String.format(Locale.US, "%.2f", r.getRefundTotal())).append(" quantities=").append(r.getQuantities()).append("\n")); return b.toString(); }
    public static String restocks(StoreService s) { StringBuilder b = new StringBuilder("RESTOCKING REPORT\n"); s.restocks().forEach(r -> b.append(r.getId()).append(" product=").append(r.getProductId()).append(" supplier=").append(r.getSupplierId()).append(" quantity=").append(r.getQuantity()).append(" total=").append(String.format(Locale.US, "%.2f", r.totalCost())).append("\n")); return b.toString(); }
}
