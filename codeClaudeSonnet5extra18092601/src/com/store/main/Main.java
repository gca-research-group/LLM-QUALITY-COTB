package com.store.main;

import com.store.cart.Cart;
import com.store.catalog.Category;
import com.store.catalog.Product;
import com.store.core.Store;
import com.store.order.Order;
import com.store.payment.Payment;
import com.store.people.Customer;
import com.store.people.LoyaltyTier;
import com.store.people.Supplier;
import com.store.promotion.BuyXGetYPromotion;
import com.store.promotion.FixedDiscountPromotion;
import com.store.promotion.PercentagePromotion;
import com.store.restock.RestockEntry;
import com.store.returns.ReturnRequest;
import com.store.shipping.ExpressShipping;
import com.store.shipping.PickupShipping;
import com.store.shipping.StandardShipping;

public class Main {

    public static void main(String[] args) {
        Store store = new Store();

        System.out.println("========== SETUP ==========");

        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        store.getCatalog().registerCategory(office);
        store.getCatalog().registerCategory(accessories);
        store.getCatalog().registerCategory(bags);

        Product p1 = new Product("P1", "Notebook", 15.0, office);
        Product p2 = new Product("P2", "Pen", 5.0, office);
        Product p3 = new Product("P3", "Mug", 20.0, accessories);
        Product p4 = new Product("P4", "Backpack", 80.0, bags);
        Product p5 = new Product("P5", "Bottle", 25.0, accessories);
        store.getCatalog().registerProduct(p1);
        store.getCatalog().registerProduct(p2);
        store.getCatalog().registerProduct(p3);
        store.getCatalog().registerProduct(p4);
        store.getCatalog().registerProduct(p5);

        store.getStockService().increaseStock("P1", 100);
        store.getStockService().increaseStock("P2", 200);
        store.getStockService().increaseStock("P3", 40);
        store.getStockService().increaseStock("P4", 15);
        store.getStockService().increaseStock("P5", 30);

        store.getCustomerRepository().register(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        store.getCustomerRepository().register(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        store.getCustomerRepository().register(new Customer("CU3", "Carla", LoyaltyTier.SILVER));

        store.getSupplierRepository().register(new Supplier("SU1", "Alpha Supplies"));
        store.getSupplierRepository().register(new Supplier("SU2", "Beta Wholesale"));

        store.getPromotionService().registerPromotion(new PercentagePromotion("PROMO1", 10.0, 200.0));
        store.getPromotionService().registerPromotion(new FixedDiscountPromotion("PROMO2", 5.0, 50.0, LoyaltyTier.GOLD));
        store.getPromotionService().registerPromotion(new BuyXGetYPromotion("PROMO3", "P2", 2, 1));

        store.getShippingService().registerRule(new StandardShipping(12.0));
        store.getShippingService().registerRule(new ExpressShipping(25.0));
        store.getShippingService().registerRule(new PickupShipping());

        System.out.println("Registered products: " + store.getCatalog().productCount());
        System.out.println("Registered customers: " + store.getCustomerRepository().count());
        System.out.println("Registered suppliers: " + store.getSupplierRepository().count());

        System.out.println();
        System.out.println("========== CART 1 (CU2) ==========");
        Cart cart1 = store.getCartService().createCart("CU2");
        store.getCartService().addProduct(cart1.getId(), "P4", 1);
        store.getCartService().addProduct(cart1.getId(), "P1", 2);
        store.getCartService().addProduct(cart1.getId(), "P2", 3);
        store.getCartService().addProduct(cart1.getId(), "P5", 1);
        System.out.println("Cart1 subtotal before changes: " + store.getCartService().getSubtotal(cart1.getId()));

        store.getCartService().updateQuantity(cart1.getId(), "P2", 6);
        System.out.println("Cart1 subtotal after quantity change: " + store.getCartService().getSubtotal(cart1.getId()));

        store.getCartService().removeProduct(cart1.getId(), "P5");
        System.out.println("Cart1 subtotal after item removal: " + store.getCartService().getSubtotal(cart1.getId()));

        System.out.println();
        System.out.println("========== ORDER ORD1 ==========");
        Order ord1 = store.getOrderService().createOrderFromCart("ORD1", cart1);
        store.getPromotionService().applyPromotion("PROMO3", ord1);
        store.getShippingService().applyShipping("EXPRESS", ord1);
        System.out.println("ORD1 subtotal: " + ord1.getSubtotal());
        System.out.println("ORD1 promotion discount: " + ord1.getPromotionDiscount() + " (" + ord1.getPromotionDescription() + ")");
        System.out.println("ORD1 shipping cost: " + ord1.getShippingCost() + " (" + ord1.getShippingDescription() + ")");
        System.out.println("ORD1 final total: " + ord1.getFinalTotal());

        Payment payment1 = store.getPaymentService().registerPayment("ORD1", ord1.getFinalTotal());
        System.out.println("Registered payment " + payment1.getId() + " for ORD1, amount = " + payment1.getAmount());
        System.out.println("ORD1 final status: " + ord1.getStatus());

        System.out.println();
        System.out.println("========== CART 2 (CU1) ==========");
        Cart cart2 = store.getCartService().createCart("CU1");
        store.getCartService().addProduct(cart2.getId(), "P3", 2);
        store.getCartService().addProduct(cart2.getId(), "P5", 1);
        System.out.println("Cart2 subtotal: " + store.getCartService().getSubtotal(cart2.getId()));

        System.out.println();
        System.out.println("========== ORDER ORD2 ==========");
        Order ord2 = store.getOrderService().createOrderFromCart("ORD2", cart2);
        store.getShippingService().applyShipping("STANDARD", ord2);
        System.out.println("ORD2 subtotal: " + ord2.getSubtotal());
        System.out.println("ORD2 promotion discount: " + ord2.getPromotionDiscount() + " (" + ord2.getPromotionDescription() + ")");
        System.out.println("ORD2 shipping cost: " + ord2.getShippingCost() + " (" + ord2.getShippingDescription() + ")");
        System.out.println("ORD2 final total: " + ord2.getFinalTotal());

        store.getOrderService().cancelOrder("ORD2");
        System.out.println("ORD2 final status: " + ord2.getStatus());

        System.out.println();
        System.out.println("========== RETURN FOR ORD1 ==========");
        ReturnRequest ret1 = store.getReturnService().createReturnRequest("ORD1");
        store.getReturnService().addReturnedItem(ret1.getId(), "P1", 1);
        double refundTotalPreview = store.getReturnService().calculateRefundTotal(ret1.getId());
        System.out.println("Return request identifier: " + ret1.getId());
        System.out.println("Returned product P1, quantity = 1");
        System.out.println("Calculated refund total (pre-processing): " + refundTotalPreview);
        store.getReturnService().processRefund(ret1.getId());
        System.out.println("Processed refund total: " + ret1.getRefundTotal());

        System.out.println();
        System.out.println("========== RESTOCKING ==========");
        RestockEntry restock1 = store.getRestockService().registerRestock("P4", "SU1", 10, 40.0);
        System.out.println("Restock entry identifier: " + restock1.getId());
        System.out.println("Restocked product: " + restock1.getProductId() + ", quantity: " + restock1.getQuantity());
        System.out.println("Restock total cost: " + restock1.getTotalCost());

        System.out.println();
        System.out.println("========== MANUAL CASHFLOW ENTRY ==========");
        store.getCashflowService().recordOutflow(50.0, "Manual operational expense: office rent");
        System.out.println("Registered manual outflow entry of 50.0 for office rent");

        System.out.println();
        System.out.println("========== CSV EXPORT ==========");
        String inventoryFile = store.getCsvExporter().exportInventory("inventory.csv");
        System.out.println("Exported inventory data to " + inventoryFile);
        String cashflowFile = store.getCsvExporter().exportCashflow("cashflow.csv");
        System.out.println("Exported cashflow data to " + cashflowFile);

        System.out.println();
        System.out.println("========== INVALID OPERATIONS ==========");
        try {
            store.getPaymentService().registerPayment("ORD2", ord2.getFinalTotal());
            System.out.println("ERROR: payment for cancelled order was not rejected");
        } catch (IllegalStateException e) {
            System.out.println("Invalid operation correctly rejected (payment for cancelled order): " + e.getMessage());
        }

        try {
            store.getOrderService().cancelOrder("ORD1");
            System.out.println("ERROR: cancellation of paid order was not rejected");
        } catch (IllegalStateException e) {
            System.out.println("Invalid operation correctly rejected (cancel paid order): " + e.getMessage());
        }

        try {
            store.getCatalog().registerProduct(new Product("P1", "Duplicate Notebook", 10.0, office));
            System.out.println("ERROR: duplicate product registration was not rejected");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid operation correctly rejected (duplicate product id): " + e.getMessage());
        }

        try {
            store.getStockService().decreaseStock("P3", 1000);
            System.out.println("ERROR: negative stock operation was not rejected");
        } catch (IllegalStateException e) {
            System.out.println("Invalid operation correctly rejected (stock would become negative): " + e.getMessage());
        }

        System.out.println();
        System.out.println("========== FINAL STOCK VALUES ==========");
        System.out.println("P1 stock: " + store.getStockService().getStock("P1"));
        System.out.println("P2 stock: " + store.getStockService().getStock("P2"));
        System.out.println("P3 stock: " + store.getStockService().getStock("P3"));
        System.out.println("P4 stock: " + store.getStockService().getStock("P4"));
        System.out.println("P5 stock: " + store.getStockService().getStock("P5"));

        System.out.println();
        System.out.println("========== REPORTS ==========");
        store.getReportService().printStockReport();
        store.getReportService().printSalesByProductReport();
        store.getReportService().printOrdersByStatusReport();
        store.getReportService().printCashflowSummaryReport();
        store.getReportService().printReturnsRefundsReport();
        store.getReportService().printRestockingReport();

        System.out.println();
        System.out.println("========== SCENARIO COMPLETE ==========");
    }
}