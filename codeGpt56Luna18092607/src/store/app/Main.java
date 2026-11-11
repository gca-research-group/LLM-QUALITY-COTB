package store.app;

import store.io.CsvExporter;
import store.model.BuyXGetYPromotion;
import store.model.Category;
import store.model.Customer;
import store.model.FixedPromotion;
import store.model.LoyaltyTier;
import store.model.Order;
import store.model.OrderStatus;
import store.model.PercentagePromotion;
import store.model.Product;
import store.model.Restock;
import store.model.ReturnRequest;
import store.model.ShippingMethod;
import store.model.ShippingRule;
import store.model.Supplier;
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

        store.addProduct(new Product("P1", "Notebook", 15.0, c1));
        store.addProduct(new Product("P2", "Pen", 5.0, c1));
        store.addProduct(new Product("P3", "Mug", 20.0, c2));
        store.addProduct(new Product("P4", "Backpack", 80.0, c3));
        store.addProduct(new Product("P5", "Bottle", 25.0, c2));

        store.increaseStock("P1", 100);
        store.increaseStock("P2", 200);
        store.increaseStock("P3", 40);
        store.increaseStock("P4", 15);
        store.increaseStock("P5", 30);

        store.addCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        store.addCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        store.addCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));

        store.addSupplier(new Supplier("SU1", "Alpha Supplies"));
        store.addSupplier(new Supplier("SU2", "Beta Wholesale"));

        PercentagePromotion tenPercent =
            new PercentagePromotion(200.0, 10.0);

        FixedPromotion goldFive =
            new FixedPromotion(50.0, 5.0, LoyaltyTier.GOLD);

        BuyXGetYPromotion buyTwoGetOne =
            new BuyXGetYPromotion("P2", 2, 1);

        store.addPromotion(tenPercent);
        store.addPromotion(goldFive);
        store.addPromotion(buyTwoGetOne);

        store.addShippingRule(
            new ShippingRule(ShippingMethod.STANDARD, 12.0)
        );

        store.addShippingRule(
            new ShippingRule(ShippingMethod.EXPRESS, 25.0)
        );

        store.addShippingRule(
            new ShippingRule(ShippingMethod.PICKUP, 0.0)
        );

        store.createCart("CART1", "CU2");

        store.addToCart("CART1", "P4", 1);
        store.addToCart("CART1", "P1", 2);
        store.addToCart("CART1", "P2", 4);
        store.addToCart("CART1", "P5", 2);

        System.out.println("Registered products: " + store.productCount());
        System.out.println("Registered customers: " + store.customerCount());
        System.out.println("Registered suppliers: " + store.supplierCount());

        System.out.println(
            "Cart subtotal before changes: " +
            store.cartSubtotal("CART1")
        );

        store.updateCart("CART1", "P2", 6);

        System.out.println(
            "Cart subtotal after quantity change: " +
            store.cartSubtotal("CART1")
        );

        store.removeFromCart("CART1", "P5");

        System.out.println(
            "Cart subtotal after item removal: " +
            store.cartSubtotal("CART1")
        );

        Order order1 = store.createOrder("ORD1", "CART1");

        store.applyPromotion("ORD1", goldFive);
        store.applyShipping("ORD1", ShippingMethod.EXPRESS);

        System.out.println("ORD1 subtotal: " + order1.getSubtotal());
        System.out.println(
            "ORD1 promotion discount: " +
            order1.getPromotionDiscount()
        );
        System.out.println(
            "ORD1 shipping cost: " +
            order1.getShipping()
        );
        System.out.println(
            "ORD1 final total: " +
            order1.getFinalTotal()
        );

        store.pay(
            "PAY1",
            "ORD1",
            order1.getFinalTotal()
        );

        System.out.println(
            "ORD1 final status: " +
            order1.getStatus()
        );

        store.createCart("CART2", "CU1");

        store.addToCart("CART2", "P3", 2);
        store.addToCart("CART2", "P5", 1);

        Order order2 = store.createOrder("ORD2", "CART2");

        store.applyShipping("ORD2", ShippingMethod.STANDARD);

        System.out.println("ORD2 subtotal: " + order2.getSubtotal());
        System.out.println(
            "ORD2 promotion discount: " +
            order2.getPromotionDiscount()
        );
        System.out.println(
            "ORD2 shipping cost: " +
            order2.getShipping()
        );
        System.out.println(
            "ORD2 final total: " +
            order2.getFinalTotal()
        );

        store.cancelOrder("ORD2");

        System.out.println(
            "ORD2 final status: " +
            order2.getStatus()
        );

        store.createReturn("RET1", "ORD1");
        store.addReturnItem("RET1", "P2", 2);

        System.out.println(
            "Return request identifier: RET1"
        );

        System.out.println("Returned: P2 x 2");

        System.out.println(
            "Refund total: " +
            store.refundTotal("RET1")
        );

        store.processRefund("RET1");

        store.restock(
            "RST1",
            "P3",
            "SU1",
            10,
            12.0
        );

        Restock restock = store.allRestocks().get(0);

        System.out.println(
            "Restock entry identifier: " +
            restock.getId()
        );

        System.out.println(
            "Restocked: " +
            restock.getProduct().getId() +
            " x " +
            restock.getQuantity()
        );

        System.out.println(
            "Restock total cost: " +
            restock.getTotalCost()
        );

        store.manualOutflow(
            "MAN1",
            20.0,
            "Store supplies"
        );

        boolean rejected = false;

        try {
            store.addToCart("CART2", "P3", 0);
        } catch (Exception exception) {
            rejected = true;
        }

        System.out.println(
            "Invalid operation rejected: " +
            rejected
        );

        System.out.println("Stock report:");

        for (Product product : store.allProducts()) {
            System.out.println(
                product.getId() +
                " = " +
                store.getStock(product.getId())
            );
        }

        System.out.println("Sales by product report:");

        for (Product product : store.allProducts()) {
            System.out.println(
                product.getId() +
                " = " +
                store.soldQuantity(product.getId())
            );
        }

        System.out.println("Orders by status report:");

        for (OrderStatus status : OrderStatus.values()) {
            System.out.println(
                status +
                " = " +
                store.orderCount(status)
            );
        }

        System.out.println("Cashflow summary report:");

        System.out.println(
            "Total inflow = " +
            store.totalInflow()
        );

        System.out.println(
            "Total outflow = " +
            store.totalOutflow()
        );

        System.out.println(
            "Net cashflow = " +
            (store.totalInflow() - store.totalOutflow())
        );

        System.out.println("Returns/refunds report:");

        for (ReturnRequest request : store.allReturns()) {
            System.out.println(
                request.getId() +
                " = " +
                request.getRefundTotal()
            );
        }

        System.out.println("Restocking report:");

        for (Restock entry : store.allRestocks()) {
            System.out.println(
                entry.getId() +
                ", " +
                entry.getProduct().getId() +
                ", " +
                entry.getSupplier().getId() +
                ", " +
                entry.getQuantity() +
                ", " +
                entry.getTotalCost()
            );
        }

        System.out.println(
            "Final stock P1 = " +
            store.getStock("P1")
        );

        System.out.println(
            "Final stock P2 = " +
            store.getStock("P2")
        );

        System.out.println(
            "Final stock P3 = " +
            store.getStock("P3")
        );

        System.out.println(
            "Final stock P4 = " +
            store.getStock("P4")
        );

        System.out.println(
            "Final stock P5 = " +
            store.getStock("P5")
        );

        System.out.println(
            "Inventory search by name Notebook: " +
            store.findProductsByName("Notebook").size()
        );

        System.out.println(
            "Products in Office category: " +
            store.findProductsByCategory("C1").size()
        );

        store.setProductActive("P5", false);
        store.setProductActive("P5", true);

        String inventoryFile =
            CsvExporter.exportInventory(
                "inventory.csv",
                store.allProducts(),
                store.stockSnapshot()
            );

        String cashflowFile =
            CsvExporter.exportCashflow(
                "cashflow.csv",
                store.allCashflow()
            );

        System.out.println(
            "Exported: " +
            inventoryFile
        );

        System.out.println(
            "Exported: " +
            cashflowFile
        );
    }
}