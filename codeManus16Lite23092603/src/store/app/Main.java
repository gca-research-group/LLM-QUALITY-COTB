package store.app;

import store.domain.*;
import store.repository.InMemoryStore;
import store.service.*;
import store.report.Reports;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        InMemoryStore db=new InMemoryStore(); StoreService s=new StoreService(db);
        s.addCategory(new Category("C1","Office")); s.addCategory(new Category("C2","Accessories")); s.addCategory(new Category("C3","Bags"));
        Product p1=new Product("P1","Notebook",15,"C1".equals("C1")?db.categories.get("C1"):null); Product p2=new Product("P2","Pen",5,db.categories.get("C1")); Product p3=new Product("P3","Mug",20,db.categories.get("C2")); Product p4=new Product("P4","Backpack",80,db.categories.get("C3")); Product p5=new Product("P5","Bottle",25,db.categories.get("C2"));
        for(Product p:List.of(p1,p2,p3,p4,p5))s.addProduct(p); p1.increaseStock(100);p2.increaseStock(200);p3.increaseStock(40);p4.increaseStock(15);p5.increaseStock(30);
        s.addCustomer(new Customer("CU1","Ana",LoyaltyTier.REGULAR));s.addCustomer(new Customer("CU2","Bruno",LoyaltyTier.GOLD));s.addCustomer(new Customer("CU3","Carla",LoyaltyTier.SILVER));s.addSupplier(new Supplier("SU1","Alpha Supplies"));s.addSupplier(new Supplier("SU2","Beta Wholesale"));
        Cart c1=s.createCart("CART1","CU2");s.addToCart("CART1","P4",1);s.addToCart("CART1","P1",2);s.addToCart("CART1","P2",5);s.addToCart("CART1","P5",1);System.out.printf("cart subtotal before changes=%.2f%n",c1.subtotal());s.updateCart("CART1","P1",3);System.out.printf("cart subtotal after quantity change=%.2f%n",c1.subtotal());s.removeFromCart("CART1","P5");System.out.printf("cart subtotal after removal=%.2f%n",c1.subtotal());
        Order o1=s.createOrder("ORD1","CART1");o1.setDiscount(Rules.goldFixed(o1,50,5)+Rules.buyXGetY(o1,"P2",2,1));o1.setShipping(Rules.shipping(ShippingType.STANDARD));System.out.printf("ORD1 subtotal=%.2f discount=%.2f shipping=%.2f total=%.2f%n",o1.subtotal(),o1.getDiscount(),o1.getShipping(),o1.total());s.pay("PAY1","ORD1",PaymentMethod.CARD);System.out.println("ORD1 status="+o1.getStatus());
        Cart c2=s.createCart("CART2","CU1");s.addToCart("CART2","P3",1);s.addToCart("CART2","P5",1);Order o2=s.createOrder("ORD2","CART2");o2.setShipping(Rules.shipping(ShippingType.EXPRESS));System.out.printf("ORD2 subtotal=%.2f discount=%.2f shipping=%.2f total=%.2f%n",o2.subtotal(),o2.getDiscount(),o2.getShipping(),o2.total());s.cancel("ORD2");System.out.println("ORD2 status="+o2.getStatus());
        Map<String,Integer> returned=new LinkedHashMap<>();returned.put("P4",1);ReturnRequest rr=s.processReturn("RET1","ORD1",returned);System.out.println("return id="+rr.id()+" items="+rr.items()+" refund="+rr.refundTotal());Restock rest=s.restock("RST1","P3","SU1",5,20);System.out.println("restock="+rest.id()+" product="+rest.productId()+" quantity="+rest.quantity()+" total="+rest.total());s.cash("MAN1",CashflowType.OUTFLOW,7,"Operational supplies");
        boolean rejected=false;try{s.addToCart("CART1","P1",-1);}catch(RuntimeException e){rejected=true;}System.out.println("invalid operation rejected="+rejected);
        s.exportCsv("inventory.csv",true);s.exportCsv("cashflow.csv",false);System.out.println("inventory.csv exported");System.out.println("cashflow.csv exported");
        Reports r=new Reports(db);System.out.println("registered products="+db.products.size());System.out.println("registered customers="+db.customers.size());System.out.println("registered suppliers="+db.suppliers.size());System.out.println("stock report="+r.stock());System.out.println("sales by product="+r.sales());System.out.println("orders by status="+r.statuses());System.out.println("cashflow summary="+r.cash());System.out.println("returns/refunds="+r.returns());System.out.println("restocking="+r.restocks());System.out.println("final stock P1="+p1.getStock()+" P2="+p2.getStock()+" P3="+p3.getStock()+" P4="+p4.getStock()+" P5="+p5.getStock());
    }
}
