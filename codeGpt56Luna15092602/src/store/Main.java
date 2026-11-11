package store;

import store.model.BuyXGetYPromotion;
import store.model.Category;
import store.model.Customer;
import store.model.FixedPromotion;
import store.model.Order;
import store.model.PercentagePromotion;
import store.model.Product;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.StandardShipping;
import store.model.ExpressShipping;
import store.model.Supplier;
import store.model.Cart;
import store.report.ReportService;
import store.service.StoreService;

public class Main {
    public static void main(String[] args) throws Exception {
        StoreService store = new StoreService();

        Category c1 = new Category("C1", "Office");
        Category c2 = new Category("C2", "Accessories");
        Category c3 = new Category("C3", "Bags");

        store.addCategory(c1);
        store.addCategory(c2);
        store.addCategory(c3);

        store.addProduct(new Product("P1", "Notebook", 15.0, c1), 100);
        store.addProduct(new Product("P2", "Pen", 5.0, c1), 200);
        store.addProduct(new Product("P3", "Mug", 20.0, c2), 40);
        store.addProduct(new Product("P4", "Backpack", 80.0, c3), 15);
        store.addProduct(new Product("P5", "Bottle", 25.0, c2), 30);

        store.registerCustomer(
                new Customer("CU1", "Ana", Customer.LoyaltyTier.REGULAR)
        );
        store.registerCustomer(
                new Customer("CU2", "Bruno", Customer.LoyaltyTier.GOLD)
        );
        store.registerCustomer(
                new Customer("CU3", "Carla", Customer.LoyaltyTier.SILVER)
        );

        store.registerSupplier(new Supplier("SU1", "Alpha Supplies"));
        store.registerSupplier(new Supplier("SU2", "Beta Wholesale"));

        store.addPromotion(new PercentagePromotion(10, 200));
        store.addPromotion(
                new FixedPromotion(5, 50, Customer.LoyaltyTier.GOLD)
        );
        store.addPromotion(new BuyXGetYPromotion("P2", 2, 1));

        Cart cart = store.createCart("CART1", "CU2");

        store.addToCart("CART1", "P4", 1);
        store.addToCart("CART1", "P1", 5);
        store.addToCart("CART1", "P2", 4);
        store.addToCart("CART1", "P5", 2);

        System.out.printf(
                "CART SUBTOTAL BEFORE CHANGES: %.2f%n",
                cart.subtotal()
        );

        store.updateCart("CART1", "P1", 7);

        System.out.printf(
                "CART SUBTOTAL AFTER QUANTITY CHANGE: %.2f%n",
                cart.subtotal()
        );

        store.removeFromCart("CART1", "P5");

        System.out.printf(
                "CART SUBTOTAL AFTER ITEM REMOVAL: %.2f%n",
                cart.subtotal()
        );

        Order order1 = store.createOrder("ORD1", "CART1");

        double discount1 = store.applyPromotion(
                "ORD1",
                new FixedPromotion(5, 50, Customer.LoyaltyTier.GOLD)
        );

        double shipping1 = store.applyShipping(
                "ORD1",
                new StandardShipping()
        );

        System.out.printf("ORD1 SUBTOTAL: %.2f%n", order1.getSubtotal());
        System.out.printf(
                "ORD1 PROMOTION DISCOUNT: %.2f%n",
                discount1
        );
        System.out.printf(
                "ORD1 SHIPPING COST: %.2f%n",
                shipping1
        );
        System.out.printf(
                "ORD1 FINAL TOTAL: %.2f%n",
                order1.getFinalTotal()
        );

        store.pay("ORD1", "PAY1");

        System.out.println(
                "ORD1 FINAL STATUS: " + order1.getStatus()
        );

        Cart cart2 = store.createCart("CART2", "CU1");

        store.addToCart("CART2", "P3", 2);
        store.addToCart("CART2", "P2", 3);

        Order order2 = store.createOrder("ORD2", "CART2");

        store.applyShipping(
                "ORD2",
                new ExpressShipping()
        );

        System.out.printf("ORD2 SUBTOTAL: %.2f%n", order2.getSubtotal());
        System.out.printf(
                "ORD2 PROMOTION DISCOUNT: %.2f%n",
                order2.getPromotionDiscount()
        );
        System.out.printf(
                "ORD2 SHIPPING COST: %.2f%n",
                order2.getShippingCost()
        );
        System.out.printf(
                "ORD2 FINAL TOTAL: %.2f%n",
                order2.getFinalTotal()
        );

        store.cancel("ORD2");

        System.out.println(
                "ORD2 FINAL STATUS: " + order2.getStatus()
        );

        ReturnRequest returnRequest = store.createReturn(
                "RET1",
                "ORD1"
        );

        store.addReturnItem("RET1", "P1", 2);

        System.out.println(
                "RETURN REQUEST ID: " + returnRequest.getId()
        );
        System.out.println("RETURNED ITEMS: P1 x2");

        double refund = store.processReturn("RET1");

        System.out.printf(
                "REFUND TOTAL: %.2f%n",
                refund
        );

        Restock restock = store.restock(
                "RST1",
                "P3",
                "SU1",
                10,
                12.0
        );

        System.out.println(
                "RESTOCK ENTRY ID: " + restock.getId()
        );

        System.out.printf(
                "RESTOCKED: %s x%d%n",
                restock.getProduct().getId(),
                restock.getQuantity()
        );

        System.out.printf(
                "RESTOCK TOTAL COST: %.2f%n",
                restock.totalCost()
        );

        store.manualOutflow(20, "Store supplies");

        try {
            store.addToCart("CART2", "P1", 0);
            System.out.println("INVALID OPERATION WAS NOT REJECTED");
        } catch (RuntimeException exception) {
            System.out.println(
                    "INVALID OPERATION REJECTED: " + exception.getMessage()
            );
        }

        System.out.println(
                "REGISTERED PRODUCTS: " + store.productCount()
        );
        System.out.println(
                "REGISTERED CUSTOMERS: " + store.customerCount()
        );
        System.out.println(
                "REGISTERED SUPPLIERS: " + store.supplierCount()
        );

        System.out.printf(
                "FINAL STOCK: P1=%d, P2=%d, P3=%d, P4=%d, P5=%d%n",
                store.stock("P1"),
                store.stock("P2"),
                store.stock("P3"),
                store.stock("P4"),
                store.stock("P5")
        );

        ReportService reports = new ReportService(store);

        reports.stockReport();
        reports.salesReport();
        reports.ordersStatusReport();
        reports.cashflowReport();
        reports.returnsReport();
        reports.restockingReport();

        store.exportInventory("inventory.csv");
        System.out.println("EXPORTED: inventory.csv");

        store.exportCashflow("cashflow.csv");
        System.out.println("EXPORTED: cashflow.csv");
    }
}