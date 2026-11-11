package store;

import store.export.CsvExporter;
import store.model.BuyXGetYPromotion;
import store.model.Cart;
import store.model.Category;
import store.model.Customer;
import store.model.ExpressShipping;
import store.model.FixedPromotion;
import store.model.Order;
import store.model.PercentagePromotion;
import store.model.PickupShipping;
import store.model.Product;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.StandardShipping;
import store.model.Supplier;
import store.repository.StoreRepository;
import store.report.ReportService;
import store.service.StoreService;

import java.util.Locale;

public class Main {
    private static void printAmount(String label, double value) {
        System.out.printf(Locale.US, "%s = %.2f%n", label, value);
    }

    public static void main(String[] args) throws Exception {
        StoreRepository repository = new StoreRepository();
        StoreService service = new StoreService(repository);
        ReportService reports = new ReportService(repository);

        Category c1 = new Category("C1", "Office");
        Category c2 = new Category("C2", "Accessories");
        Category c3 = new Category("C3", "Bags");

        repository.getCatalog().addCategory(c1);
        repository.getCatalog().addCategory(c2);
        repository.getCatalog().addCategory(c3);

        service.registerProduct(new Product("P1", "Notebook", 15.0, c1));
        service.registerProduct(new Product("P2", "Pen", 5.0, c1));
        service.registerProduct(new Product("P3", "Mug", 20.0, c2));
        service.registerProduct(new Product("P4", "Backpack", 80.0, c3));
        service.registerProduct(new Product("P5", "Bottle", 25.0, c2));

        service.initializeStock("P1", 100);
        service.initializeStock("P2", 200);
        service.initializeStock("P3", 40);
        service.initializeStock("P4", 15);
        service.initializeStock("P5", 30);

        service.registerCustomer(new Customer(
                "CU1",
                "Ana",
                Customer.LoyaltyTier.REGULAR
        ));

        service.registerCustomer(new Customer(
                "CU2",
                "Bruno",
                Customer.LoyaltyTier.GOLD
        ));

        service.registerCustomer(new Customer(
                "CU3",
                "Carla",
                Customer.LoyaltyTier.SILVER
        ));

        service.registerSupplier(new Supplier("SU1", "Alpha Supplies"));
        service.registerSupplier(new Supplier("SU2", "Beta Wholesale"));

        service.registerPromotion(
                "PROMO10",
                new PercentagePromotion(10.0, 200.0)
        );

        service.registerPromotion(
                "PROMO5GOLD",
                new FixedPromotion(5.0, 50.0, Customer.LoyaltyTier.GOLD)
        );

        service.registerPromotion(
                "PROMOP2",
                new BuyXGetYPromotion("P2", 2, 1)
        );

        service.registerShippingRule(
                "STANDARD",
                new StandardShipping(12.0)
        );

        service.registerShippingRule(
                "EXPRESS",
                new ExpressShipping(25.0)
        );

        service.registerShippingRule(
                "PICKUP",
                new PickupShipping()
        );

        System.out.println("REGISTERED PRODUCTS = " + repository.getCatalog().productCount());
        System.out.println("REGISTERED CUSTOMERS = " + repository.customers().size());
        System.out.println("REGISTERED SUPPLIERS = " + repository.suppliers().size());

        Cart cart1 = service.createCart("CART1", "CU2");

        service.addToCart("CART1", "P4", 2);
        service.addToCart("CART1", "P1", 3);
        service.addToCart("CART1", "P2", 4);
        service.addToCart("CART1", "P5", 2);

        printAmount("CART SUBTOTAL BEFORE CHANGES", cart1.subtotal());

        service.updateCart("CART1", "P2", 6);

        printAmount("CART SUBTOTAL AFTER QUANTITY CHANGE", cart1.subtotal());

        service.removeFromCart("CART1", "P5");

        printAmount("CART SUBTOTAL AFTER ITEM REMOVAL", cart1.subtotal());

        Order order1 = service.createOrder("ORD1", "CART1");

        service.applyPromotion("ORD1", "PROMO5GOLD");
        service.applyShipping("ORD1", "STANDARD");

        printAmount("ORD1 SUBTOTAL", order1.subtotal());
        printAmount("ORD1 PROMOTION DISCOUNT", order1.getPromotionDiscount());
        printAmount("ORD1 SHIPPING COST", order1.getShippingCost());
        printAmount("ORD1 FINAL TOTAL", order1.finalTotal());

        service.pay("PAY1", "ORD1");

        System.out.println("ORD1 FINAL STATUS = " + order1.getStatus());

        Cart cart2 = service.createCart("CART2", "CU1");

        service.addToCart("CART2", "P3", 2);
        service.addToCart("CART2", "P1", 1);

        Order order2 = service.createOrder("ORD2", "CART2");

        service.applyShipping("ORD2", "EXPRESS");

        printAmount("ORD2 SUBTOTAL", order2.subtotal());
        printAmount("ORD2 PROMOTION DISCOUNT", order2.getPromotionDiscount());
        printAmount("ORD2 SHIPPING COST", order2.getShippingCost());
        printAmount("ORD2 FINAL TOTAL", order2.finalTotal());

        service.cancel("ORD2");

        System.out.println("ORD2 FINAL STATUS = " + order2.getStatus());

        ReturnRequest returnRequest = service.createReturn("RET1", "ORD1");

        service.addReturnItem("RET1", "P4", 1);

        System.out.println("RETURN REQUEST IDENTIFIER = " + returnRequest.getId());

        for (ReturnRequest.ReturnedItem item : returnRequest.getItems()) {
            System.out.println(
                    "RETURNED PRODUCT = " + item.getProductId()
                            + ", QUANTITY = " + item.getQuantity()
            );
        }

        printAmount("REFUND TOTAL", returnRequest.refundTotal());

        service.processRefund("RET1");

        Restock restock = service.restock(
                "RST1",
                "P3",
                "SU1",
                10,
                12.0
        );

        System.out.println("RESTOCK ENTRY IDENTIFIER = " + restock.getId());
        System.out.println(
                "RESTOCKED PRODUCT = " + restock.getProductId()
                        + ", QUANTITY = " + restock.getQuantity()
        );
        printAmount("RESTOCK TOTAL COST", restock.totalCost());

        service.manualOutflow(
                "CF-MANUAL-1",
                30.0,
                "Store supplies"
        );

        boolean invalidOperationRejected = false;

        try {
            service.addToCart("CART2", "P1", 0);
        } catch (IllegalArgumentException exception) {
            invalidOperationRejected = true;
        }

        System.out.println(
                "INVALID OPERATION REJECTED = "
                        + invalidOperationRejected
        );

        System.out.println("FINAL STOCK P1 = " + repository.stock("P1"));
        System.out.println("FINAL STOCK P2 = " + repository.stock("P2"));
        System.out.println("FINAL STOCK P3 = " + repository.stock("P3"));
        System.out.println("FINAL STOCK P4 = " + repository.stock("P4"));
        System.out.println("FINAL STOCK P5 = " + repository.stock("P5"));

        System.out.print(reports.stockReport());
        System.out.print(reports.salesByProduct());
        System.out.print(reports.ordersByStatus());
        System.out.print(reports.cashflowSummary());
        System.out.print(reports.returnsAndRefunds());
        System.out.print(reports.restocking());

        CsvExporter.exportInventory(repository, "inventory.csv");
        System.out.println("EXPORTED inventory.csv");

        CsvExporter.exportCashflow(repository, "cashflow.csv");
        System.out.println("EXPORTED cashflow.csv");
    }
}