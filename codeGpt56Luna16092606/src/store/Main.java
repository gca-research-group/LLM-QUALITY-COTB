package store;

import store.io.CsvExporter;
import store.model.BuyXGetYPromotion;
import store.model.Category;
import store.model.Customer;
import store.model.FixedPromotion;
import store.model.Order;
import store.model.PercentagePromotion;
import store.model.Product;
import store.model.ReturnRequest;
import store.model.RestockEntry;
import store.model.ShippingMethod;
import store.model.ShippingRule;
import store.model.Supplier;
import store.report.ReportService;
import store.service.CartService;
import store.service.CashflowService;
import store.service.CatalogService;
import store.service.CustomerService;
import store.service.OrderService;
import store.service.PaymentService;
import store.service.PromotionService;
import store.service.RestockService;
import store.service.ReturnService;
import store.service.ShippingService;
import store.service.StockService;

public class Main {
    public static void main(String[] args) throws Exception {
        CatalogService catalog = new CatalogService();
        CustomerService people = new CustomerService();
        StockService stock = new StockService();
        CashflowService cashflow = new CashflowService();
        CartService carts = new CartService(catalog);
        OrderService orders = new OrderService(stock);
        PromotionService promotions = new PromotionService();
        ShippingService shipping = new ShippingService();
        PaymentService payments = new PaymentService(cashflow);
        ReturnService returns = new ReturnService(stock, cashflow);
        RestockService restocks = new RestockService(stock, cashflow);
        ReportService reports = new ReportService();
        CsvExporter csv = new CsvExporter();

        Category c1 = new Category("C1", "Office");
        Category c2 = new Category("C2", "Accessories");
        Category c3 = new Category("C3", "Bags");

        catalog.addCategory(c1);
        catalog.addCategory(c2);
        catalog.addCategory(c3);

        Product p1 = new Product("P1", "Notebook", 15.0, c1);
        Product p2 = new Product("P2", "Pen", 5.0, c1);
        Product p3 = new Product("P3", "Mug", 20.0, c2);
        Product p4 = new Product("P4", "Backpack", 80.0, c3);
        Product p5 = new Product("P5", "Bottle", 25.0, c2);

        catalog.addProduct(p1);
        catalog.addProduct(p2);
        catalog.addProduct(p3);
        catalog.addProduct(p4);
        catalog.addProduct(p5);

        stock.initialize(p1, 100);
        stock.initialize(p2, 200);
        stock.initialize(p3, 40);
        stock.initialize(p4, 15);
        stock.initialize(p5, 30);

        Customer cu1 = new Customer(
            "CU1",
            "Ana",
            Customer.LoyaltyTier.REGULAR
        );

        Customer cu2 = new Customer(
            "CU2",
            "Bruno",
            Customer.LoyaltyTier.GOLD
        );

        Customer cu3 = new Customer(
            "CU3",
            "Carla",
            Customer.LoyaltyTier.SILVER
        );

        people.registerCustomer(cu1);
        people.registerCustomer(cu2);
        people.registerCustomer(cu3);

        Supplier su1 = new Supplier(
            "SU1",
            "Alpha Supplies"
        );

        Supplier su2 = new Supplier(
            "SU2",
            "Beta Wholesale"
        );

        people.registerSupplier(su1);
        people.registerSupplier(su2);

        promotions.register(
            new PercentagePromotion(
                "10 percent over 200",
                10.0,
                200.0
            )
        );

        promotions.register(
            new FixedPromotion(
                "GOLD 5",
                5.0,
                50.0,
                Customer.LoyaltyTier.GOLD
            )
        );

        promotions.register(
            new BuyXGetYPromotion(
                "Buy 2 Get 1 Pen",
                "P2",
                2,
                1
            )
        );

        shipping.register(
            new ShippingRule(
                ShippingMethod.STANDARD,
                12.0
            )
        );

        shipping.register(
            new ShippingRule(
                ShippingMethod.EXPRESS,
                25.0
            )
        );

        shipping.register(
            new ShippingRule(
                ShippingMethod.PICKUP,
                0.0
            )
        );

        System.out.println(
            "REGISTERED PRODUCTS=" + catalog.productCount()
        );

        System.out.println(
            "REGISTERED CUSTOMERS=" + people.customerCount()
        );

        System.out.println(
            "REGISTERED SUPPLIERS=" + people.supplierCount()
        );

        carts.createCart("CART1", cu2);

        carts.add("CART1", "P4", 1);
        carts.add("CART1", "P1", 2);
        carts.add("CART1", "P2", 3);
        carts.add("CART1", "P5", 1);

        System.out.printf(
            "CART SUBTOTAL BEFORE CHANGES=%.2f%n",
            carts.subtotal("CART1")
        );

        carts.update("CART1", "P1", 4);

        System.out.printf(
            "CART SUBTOTAL AFTER QUANTITY CHANGE=%.2f%n",
            carts.subtotal("CART1")
        );

        carts.remove("CART1", "P5");

        System.out.printf(
            "CART SUBTOTAL AFTER ITEM REMOVAL=%.2f%n",
            carts.subtotal("CART1")
        );

        Order ord1 = orders.createOrder(
            "ORD1",
            carts.getCart("CART1")
        );

        orders.applyPromotion(
            "ORD1",
            promotions.all().get(1)
        );

        orders.applyShipping(
            "ORD1",
            shipping.get(ShippingMethod.STANDARD)
        );

        System.out.printf(
            "ORD1 SUBTOTAL=%.2f%n",
            ord1.getSubtotal()
        );

        System.out.printf(
            "ORD1 PROMOTION DISCOUNT=%.2f%n",
            ord1.getPromotionDiscount()
        );

        System.out.printf(
            "ORD1 SHIPPING COST=%.2f%n",
            ord1.getShippingCost()
        );

        System.out.printf(
            "ORD1 FINAL TOTAL=%.2f%n",
            ord1.getFinalTotal()
        );

        payments.pay(
            "PAY1",
            ord1,
            ord1.getFinalTotal()
        );

        System.out.println(
            "ORD1 FINAL STATUS=" + ord1.getStatus()
        );

        carts.createCart("CART2", cu1);

        carts.add("CART2", "P3", 2);
        carts.add("CART2", "P5", 1);

        Order ord2 = orders.createOrder(
            "ORD2",
            carts.getCart("CART2")
        );

        orders.applyShipping(
            "ORD2",
            shipping.get(ShippingMethod.EXPRESS)
        );

        System.out.printf(
            "ORD2 SUBTOTAL=%.2f%n",
            ord2.getSubtotal()
        );

        System.out.printf(
            "ORD2 PROMOTION DISCOUNT=%.2f%n",
            ord2.getPromotionDiscount()
        );

        System.out.printf(
            "ORD2 SHIPPING COST=%.2f%n",
            ord2.getShippingCost()
        );

        System.out.printf(
            "ORD2 FINAL TOTAL=%.2f%n",
            ord2.getFinalTotal()
        );

        orders.cancel("ORD2");

        System.out.println(
            "ORD2 FINAL STATUS=" + ord2.getStatus()
        );

        ReturnRequest returnRequest = returns.create(
            "RET1",
            ord1
        );

        returnRequest.addItem(p2, 2);

        System.out.println(
            "RETURN REQUEST ID=" + returnRequest.getId()
        );

        System.out.println(
            "RETURNED ITEMS=P2x2"
        );

        System.out.printf(
            "REFUND TOTAL=%.2f%n",
            returnRequest.getRefundTotal()
        );

        returns.process(returnRequest);

        RestockEntry restockEntry = restocks.restock(
            "RST1",
            p3,
            su1,
            10,
            12.0
        );

        System.out.println(
            "RESTOCK ENTRY ID=" + restockEntry.getId()
        );

        System.out.println(
            "RESTOCKED PRODUCT="
                + restockEntry.getProduct().getId()
                + " QUANTITY="
                + restockEntry.getQuantity()
        );

        System.out.printf(
            "RESTOCK TOTAL COST=%.2f%n",
            restockEntry.getTotalCost()
        );

        cashflow.manualOutflow(
            "MAN1",
            30.0,
            "Manual operational outflow"
        );

        try {
            carts.add("CART1", "P1", -1);
            System.out.println(
                "INVALID OPERATION REJECTED=NO"
            );
        } catch (Exception exception) {
            System.out.println(
                "INVALID OPERATION REJECTED=YES"
            );
        }

        System.out.println(
            "FINAL STOCK P1=" + p1.getStock()
                + " P2=" + p2.getStock()
                + " P3=" + p3.getStock()
                + " P4=" + p4.getStock()
                + " P5=" + p5.getStock()
        );

        reports.printStockReport(
            catalog.allProducts()
        );

        reports.printSalesByProductReport(
            orders.allOrders(),
            catalog.allProducts()
        );

        reports.printOrdersByStatusReport(
            orders.allOrders()
        );

        reports.printCashflowSummary(
            cashflow
        );

        reports.printReturnsReport(
            returns.all()
        );

        reports.printRestockingReport(
            restocks.all()
        );

        csv.exportInventory(
            catalog.allProducts(),
            "inventory.csv"
        );

        System.out.println(
            "EXPORTED inventory.csv"
        );

        csv.exportCashflow(
            cashflow.entries(),
            "cashflow.csv"
        );

        System.out.println(
            "EXPORTED cashflow.csv"
        );
    }
}