package store.app;

import java.nio.file.Path;
import store.model.Models.Category;
import store.model.Models.Customer;
import store.model.Models.LoyaltyTier;
import store.model.Models.Product;
import store.model.Models.Supplier;
import store.model.Models.ShippingType;
import store.model.Models.PaymentMethod;
import store.model.Transactions.BuyXGetYPromotion;
import store.model.Transactions.FixedPromotion;
import store.model.Transactions.Order;
import store.model.Transactions.Payment;
import store.model.Transactions.PercentagePromotion;
import store.model.Transactions.Restock;
import store.model.Transactions.ReturnRequest;
import store.repository.DataStore;
import store.service.CsvExporter;
import store.service.ReportService;
import store.service.StoreService;

public final class Main {
    public static void main(String[] args) throws Exception {
        DataStore db = new DataStore();
        StoreService store = new StoreService(db);
        Category office = new Category("C1", "Office"); Category accessories = new Category("C2", "Accessories"); Category bags = new Category("C3", "Bags");
        store.addCategory(office); store.addCategory(accessories); store.addCategory(bags);
        Product p1 = new Product("P1", "Notebook", 15.0, office); Product p2 = new Product("P2", "Pen", 5.0, office); Product p3 = new Product("P3", "Mug", 20.0, accessories); Product p4 = new Product("P4", "Backpack", 80.0, bags); Product p5 = new Product("P5", "Bottle", 25.0, accessories);
        store.addProduct(p1); store.addProduct(p2); store.addProduct(p3); store.addProduct(p4); store.addProduct(p5);
        store.increaseStock("P1", 100); store.increaseStock("P2", 200); store.increaseStock("P3", 40); store.increaseStock("P4", 15); store.increaseStock("P5", 30);
        store.addCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR)); store.addCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD)); store.addCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));
        store.addSupplier(new Supplier("SU1", "Alpha Supplies")); store.addSupplier(new Supplier("SU2", "Beta Wholesale"));
        store.registerPromotion(new PercentagePromotion(10.0, 200.0)); store.registerPromotion(new FixedPromotion(5.0, 50.0, "GOLD")); store.registerPromotion(new BuyXGetYPromotion("P2", 2, 1));
        System.out.println("REGISTERED PRODUCTS=" + db.products.size()); System.out.println("REGISTERED CUSTOMERS=" + db.customers.size()); System.out.println("REGISTERED SUPPLIERS=" + db.suppliers.size());

        store.createCart("CART1", "CU2"); store.addToCart("CART1", "P4", 1); store.addToCart("CART1", "P1", 2); store.addToCart("CART1", "P2", 3); store.addToCart("CART1", "P5", 1);
        System.out.printf("CART1 SUBTOTAL BEFORE CHANGES=%.2f%n", store.cartSubtotal("CART1")); store.updateCart("CART1", "P2", 5); System.out.printf("CART1 SUBTOTAL AFTER QUANTITY CHANGE=%.2f%n", store.cartSubtotal("CART1")); store.removeFromCart("CART1", "P5"); System.out.printf("CART1 SUBTOTAL AFTER ITEM REMOVAL=%.2f%n", store.cartSubtotal("CART1"));
        Order ord1 = store.createOrder("ORD1", "CART1"); store.applyPromotion("ORD1", new FixedPromotion(5.0, 50.0, "GOLD")); store.applyShipping("ORD1", ShippingType.EXPRESS); printOrder(ord1);
        store.pay("ORD1", new Payment("PAY1", ord1.total(), PaymentMethod.CARD)); System.out.println("ORD1 FINAL STATUS=" + ord1.getStatus());

        store.createCart("CART2", "CU1"); store.addToCart("CART2", "P3", 1); store.addToCart("CART2", "P2", 2); Order ord2 = store.createOrder("ORD2", "CART2"); store.applyPromotion("ORD2", new PercentagePromotion(10.0, 200.0)); store.applyShipping("ORD2", ShippingType.STANDARD); printOrder(ord2); store.cancel("ORD2"); System.out.println("ORD2 FINAL STATUS=" + ord2.getStatus());

        ReturnRequest ret = store.createReturn("RET1", "ORD1"); store.addReturnItem("RET1", "P1", 1); System.out.println("RETURN REQUEST=" + ret.getId()); System.out.println("RETURNED ITEMS=P1x1"); System.out.printf("REFUND TOTAL=%.2f%n", store.refundTotal("RET1")); store.processReturn("RET1");
        Restock restock = store.restock("RST1", "P3", "SU1", 10, 18.0); System.out.println("RESTOCK ENTRY=" + restock.getId()); System.out.println("RESTOCKED PRODUCT=" + restock.getProduct().getId() + " QUANTITY=" + restock.getQuantity()); System.out.printf("RESTOCK TOTAL COST=%.2f%n", restock.getTotalCost());
        store.manualOutflow("MANUAL1", 7.0, "Store supplies");
        try { store.addToCart("CART1", "P1", 0); } catch (RuntimeException ex) { System.out.println("INVALID OPERATION REJECTED=true"); }
        CsvExporter.exportInventory(db, Path.of("inventory.csv")); CsvExporter.exportCashflow(db, Path.of("cashflow.csv")); System.out.println("INVENTORY CSV EXPORTED=inventory.csv"); System.out.println("CASHFLOW CSV EXPORTED=cashflow.csv");
        System.out.println("FINAL STOCK P1=" + p1.getStock() + " P2=" + p2.getStock() + " P3=" + p3.getStock() + " P4=" + p4.getStock() + " P5=" + p5.getStock());
        ReportService reports = new ReportService(db); System.out.print(reports.stockReport()); System.out.print(reports.salesReport()); System.out.print(reports.ordersReport()); System.out.print(reports.cashflowReport()); System.out.print(reports.returnsReport()); System.out.print(reports.restockingReport());
        System.out.println("PROMOTIONS REGISTERED=" + db.promotions.size());
    }
    private static void printOrder(Order o) { System.out.printf("%s SUBTOTAL=%.2f%n%s PROMOTION DISCOUNT=%.2f%n%s SHIPPING COST=%.2f%n%s FINAL TOTAL=%.2f%n", o.getId(), o.subtotal(), o.getId(), o.getPromotionDiscount(), o.getId(), o.getShipping(), o.getId(), o.total()); }
}
