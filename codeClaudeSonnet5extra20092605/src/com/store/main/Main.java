package com.store.main;

import com.store.csv.CsvExporter;
import com.store.exception.BusinessRuleException;
import com.store.model.Cart;
import com.store.model.Category;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.model.Order;
import com.store.model.Product;
import com.store.model.RestockEntry;
import com.store.model.ReturnRequest;
import com.store.model.Supplier;
import com.store.promotion.BuyXGetYPromotion;
import com.store.promotion.FixedDiscountCustomerPromotion;
import com.store.promotion.PercentageThresholdPromotion;
import com.store.promotion.Promotion;
import com.store.report.ReportService;
import com.store.service.CartService;
import com.store.service.CashflowService;
import com.store.service.CatalogService;
import com.store.service.CustomerService;
import com.store.service.OrderService;
import com.store.service.PaymentService;
import com.store.service.PromotionService;
import com.store.service.RestockService;
import com.store.service.ReturnService;
import com.store.service.StockService;
import com.store.service.SupplierService;
import com.store.shipping.ExpressShipping;
import com.store.shipping.PickupShipping;
import com.store.shipping.ShippingRule;
import com.store.shipping.StandardShipping;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        CatalogService catalogService = new CatalogService();
        StockService stockService = new StockService(catalogService);
        CustomerService customerService = new CustomerService();
        SupplierService supplierService = new SupplierService();
        CartService cartService = new CartService();
        OrderService orderService = new OrderService(stockService);
        CashflowService cashflowService = new CashflowService();
        PromotionService promotionService = new PromotionService();
        PaymentService paymentService = new PaymentService(orderService, cashflowService);
        ReturnService returnService = new ReturnService(orderService, stockService, cashflowService);
        RestockService restockService = new RestockService(stockService, cashflowService);
        ReportService reportService = new ReportService(catalogService, orderService, cashflowService, returnService, restockService);
        CsvExporter csvExporter = new CsvExporter(catalogService, cashflowService);

        System.out.println("### Setting up categories, products, stock, customers and suppliers ###");

        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        catalogService.registerCategory(office);
        catalogService.registerCategory(accessories);
        catalogService.registerCategory(bags);

        Product notebook = new Product("P1", "Notebook", 15.0, office);
        Product pen = new Product("P2", "Pen", 5.0, office);
        Product mug = new Product("P3", "Mug", 20.0, accessories);
        Product backpack = new Product("P4", "Backpack", 80.0, bags);
        Product bottle = new Product("P5", "Bottle", 25.0, accessories);

        catalogService.addProduct(notebook);
        catalogService.addProduct(pen);
        catalogService.addProduct(mug);
        catalogService.addProduct(backpack);
        catalogService.addProduct(bottle);

        stockService.increaseStock("P1", 100);
        stockService.increaseStock("P2", 200);
        stockService.increaseStock("P3", 40);
        stockService.increaseStock("P4", 15);
        stockService.increaseStock("P5", 30);

        Customer ana = new Customer("CU1", "Ana", LoyaltyTier.REGULAR);
        Customer bruno = new Customer("CU2", "Bruno", LoyaltyTier.GOLD);
        Customer carla = new Customer("CU3", "Carla", LoyaltyTier.SILVER);
        customerService.registerCustomer(ana);
        customerService.registerCustomer(bruno);
        customerService.registerCustomer(carla);

        Supplier alpha = new Supplier("SU1", "Alpha Supplies");
        Supplier beta = new Supplier("SU2", "Beta Wholesale");
        supplierService.registerSupplier(alpha);
        supplierService.registerSupplier(beta);

        Promotion percentagePromotion = new PercentageThresholdPromotion("PROMO1", 10.0, 200.0);
        Promotion fixedGoldPromotion = new FixedDiscountCustomerPromotion("PROMO2", 5.0, 50.0, LoyaltyTier.GOLD);
        Promotion buyXGetYPromotion = new BuyXGetYPromotion("PROMO3", "P2", 2, 1);
        promotionService.registerPromotion(percentagePromotion);
        promotionService.registerPromotion(fixedGoldPromotion);
        promotionService.registerPromotion(buyXGetYPromotion);

        ShippingRule standardShipping = new StandardShipping(12.0);
        ShippingRule expressShipping = new ExpressShipping(25.0);
        ShippingRule pickupShipping = new PickupShipping();

        System.out.println("Registered products: " + catalogService.countProducts());
        System.out.println("Registered customers: " + customerService.countCustomers());
        System.out.println("Registered suppliers: " + supplierService.countSuppliers());

        System.out.println();
        System.out.println("### Catalog lookups ###");
        Product foundById = catalogService.findById("P3");
        System.out.println("Lookup by id P3 -> name: " + foundById.getName());
        List<Product> foundByName = catalogService.findByName("Pen");
        System.out.println("Lookup by name 'Pen' -> matches found: " + foundByName.size());
        List<Product> officeProducts = catalogService.findByCategory("C1");
        System.out.println("Products in category C1 (Office) -> count: " + officeProducts.size());
        catalogService.deactivateProduct("P3");
        System.out.println("P3 active status after deactivate: " + catalogService.findById("P3").isActive());
        catalogService.activateProduct("P3");
        System.out.println("P3 active status after reactivate: " + catalogService.findById("P3").isActive());

        System.out.println();
        System.out.println("### Cart 1 for customer CU2 (Bruno) ###");
        cartService.createCart("CART1", bruno);
        cartService.addProduct("CART1", backpack, 1);
        cartService.addProduct("CART1", notebook, 3);
        cartService.addProduct("CART1", pen, 4);
        cartService.addProduct("CART1", bottle, 2);
        System.out.println("Cart1 subtotal before quantity/item changes: " + cartService.subtotal("CART1"));

        cartService.updateQuantity("CART1", "P2", 6);
        System.out.println("Cart1 subtotal after quantity change: " + cartService.subtotal("CART1"));

        cartService.removeProduct("CART1", "P5");
        System.out.println("Cart1 subtotal after item removal: " + cartService.subtotal("CART1"));

        System.out.println();
        System.out.println("### Order ORD1 from Cart1 ###");
        Order order1 = orderService.createOrderFromCart("ORD1", cartService.findById("CART1"));
        Promotion promotionForOrder1 = promotionService.findById("PROMO3");
        orderService.applyPromotion("ORD1", promotionForOrder1);
        orderService.applyShipping("ORD1", standardShipping);

        System.out.println("ORD1 subtotal: " + order1.getSubtotal());
        System.out.println("ORD1 promotion discount: " + order1.getPromotionDiscount());
        System.out.println("ORD1 shipping cost: " + order1.getShippingCost());
        System.out.println("ORD1 final total: " + order1.getFinalTotal());
        System.out.println("Pickup shipping cost demo (always zero): " + pickupShipping.calculateCost(order1));

        paymentService.registerPayment("PAY1", "ORD1", order1.getFinalTotal());
        System.out.println("ORD1 final status: " + order1.getStatus());

        System.out.println();
        System.out.println("### Cart 2 for customer CU1 (Ana) ###");
        cartService.createCart("CART2", ana);
        cartService.addProduct("CART2", mug, 2);
        cartService.addProduct("CART2", pen, 3);

        System.out.println();
        System.out.println("### Order ORD2 from Cart2 ###");
        Order order2 = orderService.createOrderFromCart("ORD2", cartService.findById("CART2"));
        orderService.applyShipping("ORD2", expressShipping);

        System.out.println("ORD2 subtotal: " + order2.getSubtotal());
        System.out.println("ORD2 promotion discount: " + order2.getPromotionDiscount());
        System.out.println("ORD2 shipping cost: " + order2.getShippingCost());
        System.out.println("ORD2 final total: " + order2.getFinalTotal());

        orderService.cancelOrder("ORD2");
        System.out.println("ORD2 final status: " + order2.getStatus());

        System.out.println();
        System.out.println("### Return request for ORD1 ###");
        ReturnRequest returnRequest = returnService.createReturnRequest("RET1", "ORD1");
        returnService.addReturnedItem("RET1", notebook, 1);
        double refundPreview = returnService.calculateRefundTotal("RET1");
        System.out.println("Return request id: " + returnRequest.getId());
        System.out.println("Returned product id: P1, returned quantity: 1");
        System.out.println("Refund total (preview): " + refundPreview);

        double refundTotal = returnService.processRefund("REF1", "RET1");
        System.out.println("Refund total (processed): " + refundTotal);

        System.out.println();
        System.out.println("### Restocking operation ###");
        RestockEntry restockEntry = restockService.registerRestock("RESTOCK1", backpack, alpha, 10, 40.0);
        System.out.println("Restock entry id: " + restockEntry.getId());
        System.out.println("Restocked product id: " + restockEntry.getProduct().getId() + ", quantity: " + restockEntry.getQuantity());
        System.out.println("Restock total cost: " + restockEntry.getTotalCost());

        System.out.println();
        System.out.println("### Manual cashflow outflow ###");
        cashflowService.recordOutflow("OUT1", 100.0, "Manual operational outflow for office rent");
        System.out.println("Manual outflow entry OUT1 registered for amount: 100.0");

        System.out.println();
        System.out.println("### Final stock values ###");
        System.out.println("Final stock P1: " + stockService.getStock("P1"));
        System.out.println("Final stock P2: " + stockService.getStock("P2"));
        System.out.println("Final stock P3: " + stockService.getStock("P3"));
        System.out.println("Final stock P4: " + stockService.getStock("P4"));
        System.out.println("Final stock P5: " + stockService.getStock("P5"));

        System.out.println();
        System.out.println("### CSV export ###");
        String inventoryFile = csvExporter.exportInventory("inventory.csv");
        System.out.println("Inventory data exported to file: " + inventoryFile);
        String cashflowFile = csvExporter.exportCashflow("cashflow.csv");
        System.out.println("Cashflow data exported to file: " + cashflowFile);

        System.out.println();
        System.out.println("### Invalid operation attempt ###");
        try {
            cartService.addProduct("CART2", mug, -3);
            System.out.println("Invalid operation was NOT rejected (unexpected)");
        } catch (BusinessRuleException | IllegalArgumentException e) {
            System.out.println("Invalid operation correctly rejected: " + e.getMessage());
        }

        System.out.println();
        reportService.printStockReport();
        System.out.println();
        reportService.printSalesByProductReport();
        System.out.println();
        reportService.printOrdersByStatusReport();
        System.out.println();
        reportService.printCashflowSummaryReport();
        System.out.println();
        reportService.printReturnsRefundsReport();
        System.out.println();
        reportService.printRestockingReport();
    }
}