package store.app;

import java.nio.file.Path;
import java.util.Locale;
import store.model.StoreTypes.*;
import store.model.Rules.*;
import store.service.StoreService;
import store.report.Reports;
import store.io.CsvExporter;

public final class Main {
    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);
        StoreService s = new StoreService();
        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        s.addCategory(office); s.addCategory(accessories); s.addCategory(bags);
        s.addProduct(new Product("P1", "Notebook", 15.0, office));
        s.addProduct(new Product("P2", "Pen", 5.0, office));
        s.addProduct(new Product("P3", "Mug", 20.0, accessories));
        s.addProduct(new Product("P4", "Backpack", 80.0, bags));
        s.addProduct(new Product("P5", "Bottle", 25.0, accessories));
        s.setStock("P1", 100); s.setStock("P2", 200); s.setStock("P3", 40); s.setStock("P4", 15); s.setStock("P5", 30);
        s.registerCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        s.registerCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        s.registerCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));
        s.registerSupplier(new Supplier("SU1", "Alpha Supplies"));
        s.registerSupplier(new Supplier("SU2", "Beta Wholesale"));
        s.addPromotion(new PercentagePromotion(10, 200));
        s.addPromotion(new FixedPromotion(5, 50, LoyaltyTier.GOLD));
        s.addPromotion(new BuyXGetYPromotion("P2", 2, 1));
        System.out.println("REGISTERED PRODUCTS=" + s.products().size());
        System.out.println("REGISTERED CUSTOMERS=" + s.customers().size());
        System.out.println("REGISTERED SUPPLIERS=" + s.suppliers().size());

        Cart cart1 = s.createCart("CART1", "CU2");
        cart1.add(s.product("P4"), 1); cart1.add(s.product("P1"), 2); cart1.add(s.product("P2"), 4); cart1.add(s.product("P5"), 2);
        System.out.printf("CART1 SUBTOTAL BEFORE CHANGES=%.2f%n", cart1.subtotal());
        cart1.update("P1", 3);
        System.out.printf("CART1 SUBTOTAL AFTER QUANTITY CHANGE=%.2f%n", cart1.subtotal());
        cart1.remove("P5");
        System.out.printf("CART1 SUBTOTAL AFTER ITEM REMOVAL=%.2f%n", cart1.subtotal());
        Order ord1 = s.createOrder("ORD1", "CART1");
        s.applyPromotion("ORD1", new FixedPromotion(5, 50, LoyaltyTier.GOLD));
        s.applyShipping("ORD1", new StandardShipping(12));
        printOrder(ord1);
        s.pay("PAY1", "ORD1", PaymentMethod.CARD);
        System.out.println("ORD1 FINAL STATUS=" + ord1.getStatus());

        Cart cart2 = s.createCart("CART2", "CU1");
        cart2.add(s.product("P3"), 1); cart2.add(s.product("P2"), 2);
        Order ord2 = s.createOrder("ORD2", "CART2");
        s.applyShipping("ORD2", new PickupShipping());
        printOrder(ord2);
        s.cancel("ORD2");
        System.out.println("ORD2 FINAL STATUS=" + ord2.getStatus());

        ReturnRequest rr = s.createReturn("RET1", "ORD1");
        rr.add("P1", 1); rr.calculate();
        System.out.println("RETURN REQUEST=" + rr.getId() + " ITEMS=" + rr.getQuantities());
        System.out.printf("REFUND TOTAL=%.2f%n", rr.getRefundTotal());
        s.processReturn("RET1");
        RestockEntry restock = s.restock("RST1", "P3", "SU1", 5, 18.0);
        System.out.println("RESTOCK ENTRY=" + restock.getId() + " PRODUCT=" + restock.getProductId() + " QUANTITY=" + restock.getQuantity());
        System.out.printf("RESTOCK TOTAL COST=%.2f%n", restock.totalCost());
        s.manualOutflow(7.0, "Store supplies");
        boolean rejected = false;
        try { s.decreaseStock("P4", 1000); } catch (IllegalArgumentException ex) { rejected = true; }
        System.out.println("INVALID OPERATION REJECTED=" + rejected);

        for (Product p : s.products()) System.out.println("FINAL STOCK " + p.getId() + "=" + p.getStock());
        System.out.print(Reports.stock(s));
        System.out.print(Reports.sales(s));
        System.out.print(Reports.orders(s));
        System.out.print(Reports.cashflow(s));
        System.out.print(Reports.returns(s));
        System.out.print(Reports.restocks(s));
        Path inventory = Path.of("inventory.csv"); Path cashflow = Path.of("cashflow.csv");
        CsvExporter.inventory(s, inventory); CsvExporter.cashflow(s, cashflow);
        System.out.println("EXPORTED " + inventory.getFileName());
        System.out.println("EXPORTED " + cashflow.getFileName());
    }

    private static void printOrder(Order order) {
        System.out.printf("%s SUBTOTAL=%.2f%n", order.getId(), order.subtotal());
        System.out.printf("%s PROMOTION DISCOUNT=%.2f%n", order.getId(), order.getPromotionDiscount());
        System.out.printf("%s SHIPPING COST=%.2f%n", order.getId(), order.getShipping());
        System.out.printf("%s FINAL TOTAL=%.2f%n", order.getId(), order.total());
    }
}
