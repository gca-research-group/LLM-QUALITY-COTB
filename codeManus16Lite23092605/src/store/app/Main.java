package store.app;

import store.model.Model;
import store.report.ReportService;
import store.service.StoreService;

public class Main {
    public static void main(String[] args) throws Exception {
        StoreService store = new StoreService();
        Model.Category office = new Model.Category("C1", "Office");
        Model.Category accessories = new Model.Category("C2", "Accessories");
        Model.Category bags = new Model.Category("C3", "Bags");
        store.addCategory(office); store.addCategory(accessories); store.addCategory(bags);
        store.addProduct(new Model.Product("P1", "Notebook", 15.0, office));
        store.addProduct(new Model.Product("P2", "Pen", 5.0, office));
        store.addProduct(new Model.Product("P3", "Mug", 20.0, accessories));
        store.addProduct(new Model.Product("P4", "Backpack", 80.0, bags));
        store.addProduct(new Model.Product("P5", "Bottle", 25.0, accessories));
        store.increaseStock("P1", 100); store.increaseStock("P2", 200); store.increaseStock("P3", 40); store.increaseStock("P4", 15); store.increaseStock("P5", 30);
        store.registerCustomer(new Model.Customer("CU1", "Ana", Model.LoyaltyTier.REGULAR));
        store.registerCustomer(new Model.Customer("CU2", "Bruno", Model.LoyaltyTier.GOLD));
        store.registerCustomer(new Model.Customer("CU3", "Carla", Model.LoyaltyTier.SILVER));
        store.registerSupplier(new Model.Supplier("SU1", "Alpha Supplies")); store.registerSupplier(new Model.Supplier("SU2", "Beta Wholesale"));
        System.out.println("REGISTERED PRODUCTS=" + store.productCount());
        System.out.println("REGISTERED CUSTOMERS=" + store.customerCount());
        System.out.println("REGISTERED SUPPLIERS=" + store.supplierCount());
        store.createCart("CART1", "CU2");
        store.addToCart("CART1", "P4", 1); store.addToCart("CART1", "P1", 2); store.addToCart("CART1", "P2", 2); store.addToCart("CART1", "P5", 1);
        System.out.printf("CART SUBTOTAL BEFORE CHANGES=%.2f%n", store.cartSubtotal("CART1"));
        store.updateCartQuantity("CART1", "P1", 4);
        System.out.printf("CART SUBTOTAL AFTER QUANTITY CHANGE=%.2f%n", store.cartSubtotal("CART1"));
        store.removeFromCart("CART1", "P5");
        System.out.printf("CART SUBTOTAL AFTER ITEM REMOVAL=%.2f%n", store.cartSubtotal("CART1"));
        Model.Order order1 = store.createOrder("ORD1", "CART1");
        store.applyGoldFixedPromotion("ORD1", 50.0, 5.0); store.applyShipping("ORD1", Model.ShippingType.STANDARD);
        store.pay("ORD1", order1.total(), Model.PaymentMethod.CARD); printOrder(order1);
        store.createCart("CART2", "CU1"); store.addToCart("CART2", "P3", 2); store.addToCart("CART2", "P5", 1);
        Model.Order order2 = store.createOrder("ORD2", "CART2"); store.applyShipping("ORD2", Model.ShippingType.EXPRESS); store.cancel("ORD2"); printOrder(order2);
        Model.ReturnRequest request = store.createReturn("RET1", "ORD1"); store.addReturnedItem("RET1", "P1", 1); double refund = store.calculateRefund("RET1"); System.out.println("RETURN REQUEST ID=" + request.getId()); System.out.println("RETURNED ITEMS=" + request.getItems()); System.out.printf("REFUND TOTAL=%.2f%n", refund); store.processRefund("RET1");
        Model.Restock restock = store.restock("RST1", "P3", "SU1", 5, 10.0); System.out.println("RESTOCK ENTRY ID=" + restock.getId()); System.out.println("RESTOCKED PRODUCT=" + restock.getProduct().getId() + " QUANTITY=" + restock.getQuantity()); System.out.printf("RESTOCK TOTAL COST=%.2f%n", restock.totalCost());
        store.addManualOutflow("store utilities", 30.0);
        try { store.addToCart("CART1", "P1", 0); } catch (IllegalArgumentException ex) { System.out.println("INVALID OPERATION REJECTED=true"); }
        store.exportInventory("inventory.csv"); store.exportCashflow("cashflow.csv"); System.out.println("EXPORTED inventory.csv"); System.out.println("EXPORTED cashflow.csv");
        System.out.println("FINAL STOCK P1=" + store.findProduct("P1").getStock() + " P2=" + store.findProduct("P2").getStock() + " P3=" + store.findProduct("P3").getStock() + " P4=" + store.findProduct("P4").getStock() + " P5=" + store.findProduct("P5").getStock());
        ReportService reports = new ReportService(store); reports.printStockReport(); reports.printSalesByProductReport(); reports.printOrdersByStatusReport(); reports.printCashflowSummaryReport(); reports.printReturnsReport(); reports.printRestockingReport();
    }
    private static void printOrder(Model.Order order) { System.out.printf("%s SUBTOTAL=%.2f%n", order.getId(), order.subtotal()); System.out.printf("%s PROMOTION DISCOUNT=%.2f%n", order.getId(), order.getPromotionDiscount()); System.out.printf("%s SHIPPING COST=%.2f%n", order.getId(), order.getShippingCost()); System.out.printf("%s FINAL TOTAL=%.2f%n", order.getId(), order.total()); System.out.println(order.getId() + " FINAL STATUS=" + order.getStatus()); }
}
