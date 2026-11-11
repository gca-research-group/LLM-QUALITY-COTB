package store;

import java.io.IOException;
import java.util.Locale;
import store.model.*;
import store.repository.StoreRepository;
import store.service.StoreService;

public class Main {
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        StoreService service = new StoreService(new StoreRepository());

        service.addCategory(new Category("C1", "Office"));
        service.addCategory(new Category("C2", "Accessories"));
        service.addCategory(new Category("C3", "Bags"));

        service.addProduct(new Product(
                "P1",
                "Notebook",
                15.0,
                service.findCategoryById("C1")
        ));

        service.addProduct(new Product(
                "P2",
                "Pen",
                5.0,
                service.findCategoryById("C1")
        ));

        service.addProduct(new Product(
                "P3",
                "Mug",
                20.0,
                service.findCategoryById("C2")
        ));

        service.addProduct(new Product(
                "P4",
                "Backpack",
                80.0,
                service.findCategoryById("C3")
        ));

        service.addProduct(new Product(
                "P5",
                "Bottle",
                25.0,
                service.findCategoryById("C2")
        ));

        service.increaseStock("P1", 100);
        service.increaseStock("P2", 200);
        service.increaseStock("P3", 40);
        service.increaseStock("P4", 15);
        service.increaseStock("P5", 30);

        service.addCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        service.addCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        service.addCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));

        service.addSupplier(new Supplier("SU1", "Alpha Supplies"));
        service.addSupplier(new Supplier("SU2", "Beta Wholesale"));

        Promotion percentage = new PercentagePromotion(10.0, 200.0);
        Promotion fixedGold = new FixedPromotion(5.0, 50.0, LoyaltyTier.GOLD);
        Promotion buyTwoGetOne = new BuyXGetYPromotion("P2", 2, 1);

        service.registerPromotion(percentage);
        service.registerPromotion(fixedGold);
        service.registerPromotion(buyTwoGetOne);

        Cart cart1 = service.createCart("CART1", "CU2");

        service.addToCart("CART1", "P4", 1);
        service.addToCart("CART1", "P1", 2);
        service.addToCart("CART1", "P2", 4);
        service.addToCart("CART1", "P5", 2);

        System.out.printf(
                "Cart subtotal before changes: %.2f%n",
                cart1.getSubtotal()
        );

        service.updateCart("CART1", "P1", 5);

        System.out.printf(
                "Cart subtotal after quantity change: %.2f%n",
                cart1.getSubtotal()
        );

        service.removeFromCart("CART1", "P5");

        System.out.printf(
                "Cart subtotal after item removal: %.2f%n",
                cart1.getSubtotal()
        );

        Order ord1 = service.createOrder("ORD1", "CART1");

        service.applyPromotion("ORD1", fixedGold);
        service.applyShipping("ORD1", ShippingType.STANDARD);

        System.out.printf("ORD1 subtotal: %.2f%n", ord1.getSubtotal());
        System.out.printf(
                "ORD1 promotion discount: %.2f%n",
                ord1.getPromotionDiscount()
        );
        System.out.printf(
                "ORD1 shipping cost: %.2f%n",
                ord1.getShippingCost()
        );
        System.out.printf(
                "ORD1 final total: %.2f%n",
                ord1.getFinalTotal()
        );

        service.pay("PAY1", "ORD1");

        System.out.println("ORD1 final status: " + ord1.getStatus());

        Cart cart2 = service.createCart("CART2", "CU1");

        service.addToCart("CART2", "P3", 2);
        service.addToCart("CART2", "P5", 1);

        Order ord2 = service.createOrder("ORD2", "CART2");

        service.applyShipping("ORD2", ShippingType.EXPRESS);

        System.out.printf("ORD2 subtotal: %.2f%n", ord2.getSubtotal());
        System.out.printf(
                "ORD2 promotion discount: %.2f%n",
                ord2.getPromotionDiscount()
        );
        System.out.printf(
                "ORD2 shipping cost: %.2f%n",
                ord2.getShippingCost()
        );
        System.out.printf(
                "ORD2 final total: %.2f%n",
                ord2.getFinalTotal()
        );

        service.cancelOrder("ORD2");

        System.out.println("ORD2 final status: " + ord2.getStatus());

        ReturnRequest returnRequest = service.createReturn("RET1", "ORD1");

        service.addReturnItem("RET1", "P1", 2);

        System.out.println(
                "Return request identifier: " + returnRequest.getId()
        );

        for (ReturnItem item : returnRequest.getItems()) {
            System.out.println(
                    "Returned product: "
                            + item.getProduct().getId()
                            + ", quantity: "
                            + item.getQuantity()
            );
        }

        System.out.printf(
                "Refund total: %.2f%n",
                returnRequest.getRefundTotal()
        );

        service.processRefund("RET1");

        Restock restock = service.restock(
                "RST1",
                "P3",
                "SU1",
                10,
                12.0
        );

        System.out.println(
                "Restock entry identifier: " + restock.getId()
        );

        System.out.println(
                "Restocked product: "
                        + restock.getProduct().getId()
                        + ", quantity: "
                        + restock.getQuantity()
        );

        System.out.printf(
                "Restock total cost: %.2f%n",
                restock.getTotalCost()
        );

        service.recordCashflow(new CashflowEntry(
                "CF-M1",
                CashflowType.OUTFLOW,
                30.0,
                "Manual operational expense"
        ));

        boolean invalidRejected = false;

        try {
            service.addToCart("CART2", "P1", 0);
        } catch (IllegalArgumentException exception) {
            invalidRejected = true;
            System.out.println(
                    "Invalid operation rejected: "
                            + exception.getMessage()
            );
        }

        System.out.println(
                "Registered products: " + service.productCount()
        );

        System.out.println(
                "Registered customers: " + service.customerCount()
        );

        System.out.println(
                "Registered suppliers: " + service.supplierCount()
        );

        for (String id : new String[]{"P1", "P2", "P3", "P4", "P5"}) {
            System.out.println(
                    "Final stock " + id + ": " + service.getStock(id)
            );
        }

        System.out.println(
                "Invalid operation was rejected: " + invalidRejected
        );

        System.out.print(service.stockReport());
        System.out.print(service.salesByProductReport());
        System.out.print(service.ordersByStatusReport());
        System.out.print(service.cashflowSummaryReport());
        System.out.print(service.returnsReport());
        System.out.print(service.restockingReport());

        service.exportInventory("inventory.csv");
        System.out.println("Exported inventory.csv");

        service.exportCashflow("cashflow.csv");
        System.out.println("Exported cashflow.csv");
    }
}