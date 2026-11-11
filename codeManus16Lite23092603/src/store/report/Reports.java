package store.report;

import store.domain.*;
import store.repository.InMemoryStore;
import java.util.*;

public class Reports {
    private final InMemoryStore db; public Reports(InMemoryStore db){this.db=db;}
    public String stock(){StringBuilder s=new StringBuilder();for(Product p:db.products.values())s.append(p.getId()).append("=").append(p.getStock()).append(" ");return s.toString();}
    public String sales(){Map<String,Integer> m=new LinkedHashMap<>();for(Product p:db.products.values())m.put(p.getId(),0);for(Order o:db.orders.values())if(o.getStatus()!=OrderStatus.CANCELLED)for(Order.Item i:o.getItems())m.compute(i.product().getId(),(k,v)->v+i.quantity());return m.toString();}
    public String statuses(){Map<OrderStatus,Integer> m=new EnumMap<>(OrderStatus.class);for(OrderStatus st:OrderStatus.values())m.put(st,0);for(Order o:db.orders.values())m.put(o.getStatus(),m.get(o.getStatus())+1);return m.toString();}
    public String cash(){double in=0,out=0;for(CashflowEntry e:db.cashflow)if(e.type()==CashflowType.INFLOW)in+=e.amount();else out+=e.amount();return String.format("inflow=%.2f outflow=%.2f net=%.2f",in,out,in-out);}
    public String returns(){StringBuilder s=new StringBuilder();for(ReturnRequest r:db.returns)s.append(r.id()).append("=").append(String.format("%.2f",r.refundTotal())).append(" ");return s.toString();}
    public String restocks(){StringBuilder s=new StringBuilder();for(Restock r:db.restocks)s.append(r.id()).append("/").append(r.productId()).append("/").append(r.supplierId()).append("/").append(r.quantity()).append("/").append(String.format("%.2f",r.total())).append(" ");return s.toString();}
}
