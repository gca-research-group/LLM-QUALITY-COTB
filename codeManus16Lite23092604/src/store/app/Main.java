package store.app;

import store.model.Domain;
import store.service.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        StoreService s=new StoreService();
        Domain.Category c1=new Domain.Category("C1","Office"),c2=new Domain.Category("C2","Accessories"),c3=new Domain.Category("C3","Bags");s.addCategory(c1);s.addCategory(c2);s.addCategory(c3);
        s.addProduct(new Domain.Product("P1","Notebook",15,c1));s.addProduct(new Domain.Product("P2","Pen",5,c1));s.addProduct(new Domain.Product("P3","Mug",20,c2));s.addProduct(new Domain.Product("P4","Backpack",80,c3));s.addProduct(new Domain.Product("P5","Bottle",25,c2));
        s.increaseStock("P1",100);s.increaseStock("P2",200);s.increaseStock("P3",40);s.increaseStock("P4",15);s.increaseStock("P5",30);
        s.addCustomer(new Domain.Customer("CU1","Ana",Domain.LoyaltyTier.REGULAR));s.addCustomer(new Domain.Customer("CU2","Bruno",Domain.LoyaltyTier.GOLD));s.addCustomer(new Domain.Customer("CU3","Carla",Domain.LoyaltyTier.SILVER));s.addSupplier(new Domain.Supplier("SU1","Alpha Supplies"));s.addSupplier(new Domain.Supplier("SU2","Beta Wholesale"));
        System.out.println("registered products="+s.products.size());System.out.println("registered customers="+s.customers.size());System.out.println("registered suppliers="+s.suppliers.size());
        Domain.Cart cart=s.createCart("CART1","CU2");s.addToCart("CART1","P4",1);s.addToCart("CART1","P1",3);s.addToCart("CART1","P2",2);s.addToCart("CART1","P5",2);System.out.printf("cart subtotal before changes=%.2f%n",cart.subtotal());s.updateCart("CART1","P2",5);System.out.printf("cart subtotal after quantity change=%.2f%n",cart.subtotal());s.removeFromCart("CART1","P5");System.out.printf("cart subtotal after item removal=%.2f%n",cart.subtotal());
        Domain.Order o1=s.createOrder("ORD1","CART1");s.applyPromotion("ORD1",new Domain.FixedPromotion(5,50,Domain.LoyaltyTier.GOLD));s.applyShipping("ORD1",Domain.ShippingMethod.EXPRESS);printOrder(o1);s.pay("PAY1","ORD1");
        Domain.Cart cart2=s.createCart("CART2","CU1");s.addToCart("CART2","P3",2);s.addToCart("CART2","P5",1);Domain.Order o2=s.createOrder("ORD2","CART2");s.applyShipping("ORD2",Domain.ShippingMethod.STANDARD);printOrder(o2);s.cancel("ORD2");System.out.println("ORD2 final status="+o2.getStatus());
        Domain.ReturnRequest r=s.createReturn("RET1","ORD1");s.addReturnItem("RET1","P1",1);System.out.println("return request identifier="+r.getId()+" returned=P1:1");System.out.printf("refund total=%.2f%n",r.total());s.processReturn("RET1");
        Domain.Restock rest=s.restock("RST1","P3","SU1",10,12);System.out.println("restock entry identifier="+rest.getId()+" product="+rest.getProduct().getId()+" quantity="+rest.getQuantity()+" total="+String.format("%.2f",rest.getTotal()));s.manualOutflow("Store supplies",8);
        try{s.addToCart("CART2","P3",0);System.out.println("invalid operation was rejected=false");}catch(IllegalArgumentException e){System.out.println("invalid operation was rejected=true");}
        CsvExporter.inventory(s,"inventory.csv");CsvExporter.cashflow(s,"cashflow.csv");System.out.println("inventory.csv exported");System.out.println("cashflow.csv exported");
        System.out.println("STOCK REPORT");s.stockReport().forEach((k,v)->System.out.println(k+"="+v));System.out.println("SALES BY PRODUCT REPORT");s.salesReport().forEach((k,v)->System.out.println(k+"="+v));System.out.println("ORDERS BY STATUS REPORT");s.orderReport().forEach((k,v)->System.out.println(k+"="+v));System.out.printf("CASHFLOW SUMMARY inflow=%.2f outflow=%.2f net=%.2f%n",s.inflow(),s.outflow(),s.inflow()-s.outflow());System.out.println("RETURNS/REFUNDS REPORT");s.returns.forEach(x->System.out.printf("%s=%.2f%n",x.getId(),x.total()));System.out.println("RESTOCKING REPORT");s.restocks.forEach(x->System.out.printf("%s product=%s supplier=%s quantity=%d total=%.2f%n",x.getId(),x.getProduct().getId(),x.getSupplier().getId(),x.getQuantity(),x.getTotal()));System.out.println("FINAL STOCK");for(String id:List.of("P1","P2","P3","P4","P5"))System.out.println(id+"="+s.product(id).getStock());
    }
    private static void printOrder(Domain.Order o){System.out.printf("%s subtotal=%.2f promotion discount=%.2f shipping cost=%.2f final total=%.2f status=%s%n",o.getId(),o.getSubtotal(),o.getPromotionDiscount(),o.getShipping(),o.getTotal(),o.getStatus());}
}
