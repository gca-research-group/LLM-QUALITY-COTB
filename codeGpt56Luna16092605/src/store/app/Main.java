package store.app;

import store.model.BuyXGetYPromotion;
import store.model.CashflowType;
import store.model.Category;
import store.model.Customer;
import store.model.ExpressShipping;
import store.model.FixedPromotion;
import store.model.LoyaltyTier;
import store.model.Order;
import store.model.PercentagePromotion;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.model.StandardShipping;
import store.model.Supplier;
import store.repository.StoreRepository;
import store.report.ReportService;
import store.service.StoreService;
import store.model.Cart;

public class Main {
    private static void line(String label, Object value) {
        System.out.println(label + ": " + value);
    }

    public static void main(String[] args) throws Exception {
        StoreRepository repository = new StoreRepository();
        StoreService service = new StoreService(repository);
        ReportService reports = new ReportService(service);

        service.registerCategory(new Category("C1", "Office"));
        service.registerCategory(new Category("C2", "Accessories"));
        service.registerCategory(new Category("C3", "Bags"));

        service.registerProduct(
                new Product(
                        "P1",
                        "Notebook",
                        15.0,
                        repository.category("C1")));

        service.registerProduct(
                new Product(
                        "P2",
                        "Pen",
                        5.0,
                        repository.category("C1")));

        service.registerProduct(
                new Product(
                        "P3",
                        "Mug",
                        20.0,
                        repository.category("C2")));

        service.registerProduct(
                new Product(
                        "P4",
                        "Backpack",
                        80.0,
                        repository.category("C3")));

        service.registerProduct(
                new Product(
                        "P5",
                        "Bottle",
                        25.0,
                        repository.category("C2")));

        service.increaseStock("P1", 100);
        service.increaseStock("P2", 200);
        service.increaseStock("P3", 40);
        service.increaseStock("P4", 15);
        service.increaseStock("P5", 30);

        service.registerCustomer(
                new Customer(
                        "CU1",
                        "Ana",
                        LoyaltyTier.REGULAR));

        service.registerCustomer(
                new Customer(
                        "CU2",
                        "Bruno",
                        LoyaltyTier.GOLD));

        service.registerCustomer(
                new Customer(
                        "CU3",
                        "Carla",
                        LoyaltyTier.SILVER));

        service.registerSupplier(
                new Supplier(
                        "SU1",
                        "Alpha Supplies"));

        service.registerSupplier(
                new Supplier(
                        "SU2",
                        "Beta Wholesale"));

        service.registerPromotion(
                new PercentagePromotion(
                        10.0,
                        200.0));

        service.registerPromotion(
                new FixedPromotion(
                        5.0,
                        50.0,
                        LoyaltyTier.GOLD));

        service.registerPromotion(
                new BuyXGetYPromotion(
                        "P2",
                        2,
                        1));

        Cart cart1 = service.createCart("CART1", "CU2");

        service.addToCart("CART1", "P4", 2);
        service.addToCart("CART1", "P1", 4);
        service.addToCart("CART1", "P2", 3);
        service.addToCart("CART1", "P5", 2);

        line("PRODUCTS REGISTERED", service.productCount());
        line("CUSTOMERS REGISTERED", service.customerCount());
        line("SUPPLIERS REGISTERED", service.supplierCount());

        line(
                "CART1 SUBTOTAL BEFORE CHANGES",
                cart1.getSubtotal());

        service.updateCart("CART1", "P1", 5);

        line(
                "CART1 SUBTOTAL AFTER QUANTITY CHANGE",
                cart1.getSubtotal());

        service.removeFromCart("CART1", "P5");

        line(
                "CART1 SUBTOTAL AFTER ITEM REMOVAL",
                cart1.getSubtotal());

        Order order1 = service.createOrder("ORD1", "CART1");

        service.applyPromotion(
                "ORD1",
                service.promotions().get(0));

        service.applyShipping(
                "ORD1",
                new StandardShipping(12.0));

        line("ORD1 SUBTOTAL", order1.getSubtotal());
        line(
                "ORD1 PROMOTION DISCOUNT",
                order1.getPromotionDiscount());
        line(
                "ORD1 SHIPPING COST",
                order1.getShippingCost());
        line(
                "ORD1 FINAL TOTAL",
                order1.getFinalTotal());

        service.payOrder("PAY1", "ORD1");

        line(
                "ORD1 FINAL STATUS",
                order1.getStatus());

        Cart cart2 = service.createCart("CART2", "CU1");

        service.addToCart("CART2", "P1", 2);
        service.addToCart("CART2", "P2", 2);

        Order order2 = service.createOrder("ORD2", "CART2");

        service.applyShipping(
                "ORD2",
                new ExpressShipping(25.0));

        line("ORD2 SUBTOTAL", order2.getSubtotal());
        line(
                "ORD2 PROMOTION DISCOUNT",
                order2.getPromotionDiscount());
        line(
                "ORD2 SHIPPING COST",
                order2.getShippingCost());
        line(
                "ORD2 FINAL TOTAL",
                order2.getFinalTotal());

        service.cancelOrder("ORD2");

        line(
                "ORD2 FINAL STATUS",
                order2.getStatus());

        ReturnRequest returnRequest =
                service.createReturn("RET1", "ORD1");

        service.addReturnItem(
                "RET1",
                "P4",
                1);

        line(
                "RETURN REQUEST IDENTIFIER",
                returnRequest.getId());

        line(
                "RETURNED ITEMS",
                returnRequest.getQuantities());

        line(
                "REFUND TOTAL",
                service.refundTotal("RET1"));

        service.processRefund("RET1");

        RestockEntry restock =
                service.restock(
                        "RST1",
                        "P3",
                        "SU1",
                        10,
                        20.0);

        line(
                "RESTOCK ENTRY IDENTIFIER",
                restock.getId());

        line(
                "RESTOCKED PRODUCT AND QUANTITY",
                restock.getProductId()
                        + " / "
                        + restock.getQuantity());

        line(
                "RESTOCK TOTAL COST",
                restock.getTotalCost());

        service.recordCashflow(
                "CF-MANUAL-1",
                CashflowType.OUTFLOW,
                30.0,
                "Manual operational outflow");

        boolean rejected = false;

        try {
            service.addToCart(
                    "CART2",
                    "P3",
                    0);
        } catch (IllegalArgumentException exception) {
            rejected = true;
        }

        line(
                "INVALID OPERATION REJECTED",
                rejected);

        line(
                "FINAL STOCK P1",
                repository.product("P1").getStockQuantity());

        line(
                "FINAL STOCK P2",
                repository.product("P2").getStockQuantity());

        line(
                "FINAL STOCK P3",
                repository.product("P3").getStockQuantity());

        line(
                "FINAL STOCK P4",
                repository.product("P4").getStockQuantity());

        line(
                "FINAL STOCK P5",
                repository.product("P5").getStockQuantity());

        service.exportInventory("inventory.csv");
        service.exportCashflow("cashflow.csv");

        reports.printStockReport();
        reports.printSalesByProductReport();
        reports.printOrdersByStatusReport();
        reports.printCashflowSummaryReport();
        reports.printReturnsReport();
        reports.printRestockingReport();
    }
}