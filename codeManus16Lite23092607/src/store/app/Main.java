package store.app;

import store.domain.Domain;
import store.io.CsvExporter;
import store.report.Reports;
import store.service.StoreService;
import java.util.Locale;

public final class Main {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        StoreService s = new StoreService();
        Domain.Category office = new Domain.Category("C1", "Office");
        Domain.Category accessories = new Domain.Category("C2", "Accessories");
        Domain.Category bags = new Domain.Category("C3", "Bags");
        s.addCategory(office); s.addCategory(accessories); s.addCategory(bags);
        s.addProduct(new Domain.Product("P1", "Notebook", 15.0, office, 100));
        s.addProduct(new Domain.Product("P2", "Pen", 5.0, office, 200));
        s.addProduct(new Domain.Product("P3", "Mug", 20.0, accessories, 40));
        s.addProduct(new Domain.Product("P4", "Backpack", 80.0, bags, 15));
        s.addProduct(new Domain.Product("P5", "Bottle", 25.0, accessories, 30));
        s.addCustomer(new Domain.Customer("CU1", "Ana", Domain.LoyaltyTier.REGULAR));
        s.addCustomer(new Domain.Customer("CU2", "Bruno", Domain.LoyaltyTier.GOLD));
        s.addCustomer(new Domain.Customer("CU3", "Carla", Domain.LoyaltyTier.SILVER));
        s.addSupplier(new Domain.Supplier("SU1", "Alpha Supplies"));
        s.addSupplier(new Domain.Supplier("SU2", "Beta Wholesale"));
        s.addPromotion(new Domain.PercentagePromotion(10.0, 200.0));
        s.addPromotion(new Domain.FixedPromotion(5.0, 50.0, Domain.LoyaltyTier.GOLD));
        s.addPromotion(new Domain.BuyXGetYPromotion(s.product("P2"), 2, 1));
        s.setShipping(Domain.ShippingMethod.STANDARD, 12.0); s.setShipping(Domain.ShippingMethod.EXPRESS, 25.0); s.setShipping(Domain.ShippingMethod.PICKUP, 0.0);
        System.out.println("PRODUCTS=" + s.products().size());
        System.out.println("CUSTOMERS=" + s.customers().size());
        System.out.println("SUPPLIERS=" + s.suppliers().size());
        s.createCart("CART1", "CU2"); s.addToCart("CART1", "P4", 1); s.addToCart("CART1", "P1", 2); s.addToCart("CART1", "P2", 2); s.addToCart("CART1", "P5", 1);
        System.out.printf("CART1_SUBTOTAL_BEFORE=%.2f%n", s.cart("CART1").subtotal());
        s.updateCart("CART1", "P2", 5); System.out.printf("CART1_SUBTOTAL_AFTER_UPDATE=%.2f%n", s.cart("CART1").subtotal());
        s.removeFromCart("CART1", "P5"); System.out.printf("CART1_SUBTOTAL_AFTER_REMOVAL=%.2f%n", s.cart("CART1").subtotal());
        Domain.Order o1 = s.createOrder("ORD1", "CART1"); s.applyPromotion("ORD1", new Domain.FixedPromotion(5.0, 50.0, Domain.LoyaltyTier.GOLD)); s.applyShipping("ORD1", Domain.ShippingMethod.EXPRESS);
        printOrder(o1);
        s.pay("ORD1", Domain.PaymentMethod.CARD); System.out.println("ORD1_STATUS=" + o1.getStatus());
        s.createCart("CART2", "CU1"); s.addToCart("CART2", "P2", 3); s.addToCart("CART2", "P3", 1);
        Domain.Order o2 = s.createOrder("ORD2", "CART2"); s.applyShipping("ORD2", Domain.ShippingMethod.STANDARD); printOrder(o2); s.cancel("ORD2"); System.out.println("ORD2_STATUS=" + o2.getStatus());
        Domain.ReturnRequest rr = s.createReturn("RET1", "ORD1"); s.addReturnItem("RET1", "P1", 1); System.out.println("RETURN_ID=" + rr.getId() + " ITEMS=P1:1 REFUND=" + String.format("%.2f", rr.total())); s.processRefund("RET1");
        Domain.Restock restock = s.restock("RST1", "P3", "SU1", 10, 18.0); System.out.println("RESTOCK_ID=" + restock.getId() + " PRODUCT=" + restock.getProduct().getId() + " QUANTITY=" + restock.getQuantity() + " TOTAL=" + String.format("%.2f", restock.totalCost()));
        s.manualOutflow("Store cleaning", 30.0);
        s.product("P3").setActive(false); boolean rejected = false; try { s.addToCart("CART2", "P3", 1); } catch (RuntimeException ex) { rejected = true; } System.out.println("INVALID_OPERATION_REJECTED=" + rejected);
        CsvExporter.inventory(s, "inventory.csv"); CsvExporter.cashflow(s, "cashflow.csv"); System.out.println("EXPORTED=inventory.csv"); System.out.println("EXPORTED=cashflow.csv");
        System.out.println("FINAL_STOCK P1=" + s.product("P1").getStock() + " P2=" + s.product("P2").getStock() + " P3=" + s.product("P3").getStock() + " P4=" + s.product("P4").getStock() + " P5=" + s.product("P5").getStock());
        System.out.print(Reports.stock(s)); System.out.print(Reports.salesByProduct(s)); System.out.print(Reports.ordersByStatus(s)); System.out.print(Reports.cashflow(s)); System.out.print(Reports.returns(s)); System.out.print(Reports.restocking(s));
    }
    private static void printOrder(Domain.Order o) { System.out.printf("%s_SUBTOTAL=%.2f%n", o.getId(), o.subtotal()); System.out.printf("%s_PROMOTION_DISCOUNT=%.2f%n", o.getId(), o.getPromotionDiscount()); System.out.printf("%s_SHIPPING=%.2f%n", o.getId(), o.getShippingCost()); System.out.printf("%s_FINAL_TOTAL=%.2f%n", o.getId(), o.total()); }
}
