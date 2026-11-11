package store.app;

import store.model.*;
import store.repository.StoreRepository;
import store.service.StoreService;
import store.report.ReportService;
import java.util.Locale;

public class Main {
    private static void line(String label, double value) {
        System.out.printf(Locale.US, "%s = %.2f%n", label, value);
    }

    public static void main(String[] args) {
        StoreRepository repository = new StoreRepository();
        StoreService service = new StoreService(repository);
        ReportService reports = new ReportService(repository);

        Category c1 = new Category("C1", "Office");
        Category c2 = new Category("C2", "Accessories");
        Category c3 = new Category("C3", "Bags");

        service.addCategory(c1);
        service.addCategory(c2);
        service.addCategory(c3);

        service.addProduct(new Product("P1", "Notebook", 15.0, c1));
        service.addProduct(new Product("P2", "Pen", 5.0, c1));
        service.addProduct(new Product("P3", "Mug", 20.0, c2));
        service.addProduct(new Product("P4", "Backpack", 80.0, c3));
        service.addProduct(new Product("P5", "Bottle", 25.0, c2));

        service.increaseStock("P1", 100);
        service.increaseStock("P2", 200);
        service.increaseStock("P3", 40);
        service.increaseStock("P4", 15);
        service.increaseStock("P5", 30);

        service.addCustomer(new Customer("CU1", "Ana", Customer.LoyaltyTier.REGULAR));
        service.addCustomer(new Customer("CU2", "Bruno", Customer.LoyaltyTier.GOLD));
        service.addCustomer(new Customer("CU3", "Carla", Customer.LoyaltyTier.SILVER));

        service.addSupplier(new Supplier("SU1", "Alpha Supplies"));
        service.addSupplier(new Supplier("SU2", "Beta Wholesale"));

        Promotion percent = new Promotions.PercentageThreshold(0.10, 200.0);
        Promotion fixedGold = new Promotions.FixedCustomerThreshold(5.0, 50.0, Customer.LoyaltyTier.GOLD);
        Promotion buy2get1 = new Promotions.BuyXGetY("P2", 2, 1);

        service.addPromotion(percent);
        service.addPromotion(fixedGold);
        service.addPromotion(buy2get1);

        ShippingRule standard = new ShippingRule(ShippingRule.Type.STANDARD, 12.0);
        ShippingRule express = new ShippingRule(ShippingRule.Type.EXPRESS, 25.0);
        ShippingRule pickup = new ShippingRule(ShippingRule.Type.PICKUP, 0.0);

        System.out.println("Registered products = " + repository.products().size());
        System.out.println("Registered customers = " + repository.customers().size());
        System.out.println("Registered suppliers = " + repository.suppliers().size());

        Cart cart = service.createCart("CART1", "CU2");
        service.addToCart("CART1", "P4", 2);
        service.addToCart("CART1", "P1", 3);
        service.addToCart("CART1", "P2", 4);
        service.addToCart("CART1", "P5", 2);

        line("Cart subtotal before changes", service.cartSubtotal("CART1"));

        service.updateCart("CART1", "P1", 4);
        line("Cart subtotal after quantity change", service.cartSubtotal("CART1"));

        service.removeFromCart("CART1", "P5");
        line("Cart subtotal after item removal", service.cartSubtotal("CART1"));

        Order order1 = service.createOrder("ORD1", "CART1");
        service.applyPromotion("ORD1", percent);
        service.applyShipping("ORD1", express);

        line("ORD1 subtotal", order1.subtotal());
        line("ORD1 promotion discount", order1.getPromotionDiscount());
        line("ORD1 shipping cost", order1.getShippingCost());
        line("ORD1 final total", order1.finalTotal());

        service.pay("PAY1", "ORD1");
        System.out.println("ORD1 final status = " + order1.getStatus());

        Cart cart2 = service.createCart("CART2", "CU1");
        service.addToCart("CART2", "P3", 2);
        service.addToCart("CART2", "P2", 3);

        Order order2 = service.createOrder("ORD2", "CART2");
        service.applyShipping("ORD2", standard);

        line("ORD2 subtotal", order2.subtotal());
        line("ORD2 promotion discount", order2.getPromotionDiscount());
        line("ORD2 shipping cost", order2.getShippingCost());
        line("ORD2 final total", order2.finalTotal());

        service.cancel("ORD2");
        System.out.println("ORD2 final status = " + order2.getStatus());

        ReturnRequest returnRequest = service.createReturn("RET1", "ORD1");
        service.addReturnItem("RET1", "P4", 1);

        System.out.println("Return request identifier = " + returnRequest.getId());
        System.out.println("Returned product identifiers and quantities = " + returnRequest.getItems());

        line("Refund total", service.calculateRefund("RET1"));
        service.processRefund("RET1");

        Restock restock = service.restock("RST1", "P3", "SU1", 5, 18.0);

        System.out.println("Restock entry identifier = " + restock.getId());
        System.out.println(
                "Restocked product identifier and quantity = " +
                restock.getProduct().getId() + " / " + restock.getQuantity()
        );
        line("Restock total cost", restock.totalCost());

        service.cash(
                "CF-MANUAL",
                CashflowEntry.Type.OUTFLOW,
                20.0,
                "Manual operational outflow"
        );

        try {
            service.addToCart("CART2", "P1", 0);
            System.out.println("Invalid operation was rejected = false");
        } catch (Exception e) {
            System.out.println("Invalid operation was rejected = true (" + e.getMessage() + ")");
        }

        System.out.println("Final stock P1 = " + service.stock("P1"));
        System.out.println("Final stock P2 = " + service.stock("P2"));
        System.out.println("Final stock P3 = " + service.stock("P3"));
        System.out.println("Final stock P4 = " + service.stock("P4"));
        System.out.println("Final stock P5 = " + service.stock("P5"));

        System.out.println(reports.stockReport());
        System.out.println(reports.salesByProduct());
        System.out.println(reports.ordersByStatus());
        System.out.println(reports.cashflow(service.inflow(), service.outflow()));
        System.out.println(reports.returns());
        System.out.println(reports.restocks());

        service.exportInventory("inventory.csv");
        System.out.println("inventory.csv exported = true");

        service.exportCashflow("cashflow.csv");
        System.out.println("cashflow.csv exported = true");

        System.out.println("CSV filenames: inventory.csv, cashflow.csv");
    }
}