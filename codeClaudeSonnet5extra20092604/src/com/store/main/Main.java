package com.store.main;

import com.store.csv.CsvExportService;
import com.store.exception.StoreOperationException;
import com.store.model.Category;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.model.Product;
import com.store.model.Supplier;
import com.store.order.Order;
import com.store.promotion.BuyXGetYPromotion;
import com.store.promotion.FixedDiscountPromotion;
import com.store.promotion.PercentagePromotion;
import com.store.repository.CartRepository;
import com.store.repository.CashflowRepository;
import com.store.repository.CategoryRepository;
import com.store.repository.CustomerRepository;
import com.store.repository.OrderRepository;
import com.store.repository.ProductRepository;
import com.store.repository.RestockRepository;
import com.store.repository.ReturnRequestRepository;
import com.store.repository.SupplierRepository;
import com.store.restock.RestockOperation;
import com.store.returns.ReturnItem;
import com.store.returns.ReturnRequest;
import com.store.service.CartService;
import com.store.service.CashflowService;
import com.store.service.CatalogService;
import com.store.service.CustomerService;
import com.store.service.OrderService;
import com.store.service.PaymentService;
import com.store.service.PromotionService;
import com.store.service.ReportService;
import com.store.service.RestockService;
import com.store.service.ReturnService;
import com.store.service.ShippingService;
import com.store.service.StockService;
import com.store.service.SupplierService;
import com.store.shipping.ExpressShipping;
import com.store.shipping.PickupShipping;
import com.store.shipping.StandardShipping;

public class Main {

    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        SupplierRepository supplierRepository = new SupplierRepository();
        CartRepository cartRepository = new CartRepository();
        OrderRepository orderRepository = new OrderRepository();
        ReturnRequestRepository returnRequestRepository = new ReturnRequestRepository();
        RestockRepository restockRepository = new RestockRepository();
        CashflowRepository cashflowRepository = new CashflowRepository();

        CatalogService catalogService = new CatalogService(productRepository, categoryRepository);
        StockService stockService = new StockService(productRepository);
        CustomerService customerService = new CustomerService(customerRepository);
        SupplierService supplierService = new SupplierService(supplierRepository);
        CartService cartService = new CartService(cartRepository, customerService, catalogService);
        CashflowService cashflowService = new CashflowService(cashflowRepository);
        OrderService orderService = new OrderService(orderRepository, cartService, stockService);
        PaymentService paymentService = new PaymentService(orderService, cashflowService);
        ReturnService returnService = new ReturnService(returnRequestRepository, orderService, stockService, cashflowService, catalogService);
        RestockService restockService = new RestockService(restockRepository, catalogService, supplierService, stockService, cashflowService);
        PromotionService promotionService = new PromotionService();
        ShippingService shippingService = new ShippingService();
        ReportService reportService = new ReportService(catalogService, orderService, cashflowService, returnService, restockService);
        CsvExportService csvExportService = new CsvExportService(catalogService, cashflowService);

        System.out.println("=== SETUP ===");

        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        catalogService.registerCategory(office);
        catalogService.registerCategory(accessories);
        catalogService.registerCategory(bags);

        Product p1 = new Product("P1", "Notebook", 15.0, office);
        Product p2 = new Product("P2", "Pen", 5.0, office);
        Product p3 = new Product("P3", "Mug", 20.0, accessories);
        Product p4 = new Product("P4", "Backpack", 80.0, bags);
        Product p5 = new Product("P5", "Bottle", 25.0, accessories);
        catalogService.registerProduct(p1);
        catalogService.registerProduct(p2);
        catalogService.registerProduct(p3);
        catalogService.registerProduct(p4);
        catalogService.registerProduct(p5);

        stockService.increaseStock("P1", 100);
        stockService.increaseStock("P2", 200);
        stockService.increaseStock("P3", 40);
        stockService.increaseStock("P4", 15);
        stockService.increaseStock("P5", 30);

        Customer cu1 = new Customer("CU1", "Ana", LoyaltyTier.REGULAR);
        Customer cu2 = new Customer("CU2", "Bruno", LoyaltyTier.GOLD);
        Customer cu3 = new Customer("CU3", "Carla", LoyaltyTier.SILVER);
        customerService.registerCustomer(cu1);
        customerService.registerCustomer(cu2);
        customerService.registerCustomer(cu3);

        Supplier su1 = new Supplier("SU1", "Alpha Supplies");
        Supplier su2 = new Supplier("SU2", "Beta Wholesale");
        supplierService.registerSupplier(su1);
        supplierService.registerSupplier(su2);

        promotionService.register(new PercentagePromotion("PERCENT10_OVER200", 10.0, 200.0));
        promotionService.register(new FixedDiscountPromotion("FIXED5_GOLD_OVER50", 5.0, 50.0, LoyaltyTier.GOLD));
        promotionService.register(new BuyXGetYPromotion("BUY2GET1_PEN", "P2", 2, 1));

        shippingService.registerRule("STANDARD", new StandardShipping(12.0));
        shippingService.registerRule("EXPRESS", new ExpressShipping(25.0));
        shippingService.registerRule("PICKUP", new PickupShipping());

        System.out.println("Registered products: " + catalogService.listAll().size());
        System.out.println("Registered customers: " + customerService.count());
        System.out.println("Registered suppliers: " + supplierService.count());

        System.out.println();
        System.out.println("=== CART 1 (CU2) ===");
        cartService.createCart("CART1", "CU2");
        cartService.addProductToCart("CART1", "P4", 1);
        cartService.addProductToCart("CART1", "P1", 3);
        cartService.addProductToCart("CART1", "P2", 4);
        cartService.addProductToCart("CART1", "P5", 2);
        System.out.println("Cart subtotal before changes: " + cartService.getCartSubtotal("CART1"));

        cartService.updateCartItemQuantity("CART1", "P2", 6);
        System.out.println("Cart subtotal after quantity change: " + cartService.getCartSubtotal("CART1"));

        cartService.removeCartItem("CART1", "P5");
        System.out.println("Cart subtotal after item removal: " + cartService.getCartSubtotal("CART1"));

        System.out.println();
        System.out.println("=== ORDER ORD1 ===");
        Order ord1 = orderService.createOrderFromCart("ORD1", "CART1");
        System.out.println("ORD1 subtotal: " + ord1.getSubtotal());

        orderService.applyBestPromotion("ORD1", promotionService.getAll());
        System.out.println("ORD1 applied promotion: " + ord1.getAppliedPromotionName());
        System.out.println("ORD1 promotion discount: " + ord1.getPromotionDiscount());

        orderService.applyShipping("ORD1", shippingService.getRule("STANDARD"));
        System.out.println("ORD1 shipping cost: " + ord1.getShippingCost());

        System.out.println("ORD1 final total: " + ord1.getFinalTotal());

        paymentService.registerPayment("PAY1", "ORD1", ord1.getFinalTotal());
        System.out.println("ORD1 status after payment: " + ord1.getStatus());

        System.out.println();
        System.out.println("=== CART 2 (CU1) ===");
        cartService.createCart("CART2", "CU1");
        cartService.addProductToCart("CART2", "P1", 2);
        cartService.addProductToCart("CART2", "P3", 1);
        System.out.println("Cart2 subtotal: " + cartService.getCartSubtotal("CART2"));

        System.out.println();
        System.out.println("=== ORDER ORD2 ===");
        Order ord2 = orderService.createOrderFromCart("ORD2", "CART2");
        System.out.println("ORD2 subtotal: " + ord2.getSubtotal());

        orderService.applyShipping("ORD2", shippingService.getRule("EXPRESS"));
        System.out.println("ORD2 shipping cost: " + ord2.getShippingCost());
        System.out.println("ORD2 promotion discount: " + ord2.getPromotionDiscount());
        System.out.println("ORD2 final total: " + ord2.getFinalTotal());

        orderService.cancelOrder("ORD2");
        System.out.println("ORD2 status after cancellation: " + ord2.getStatus());

        System.out.println();
        System.out.println("=== RETURN REQUEST FOR ORD1 ===");
        ReturnRequest returnRequest = returnService.createReturnRequest("RET1", "ORD1");
        System.out.println("Return request id: " + returnRequest.getId());
        returnService.addReturnItem("RET1", "P1", 1);
        for (ReturnItem item : returnRequest.getItems()) {
            System.out.println("Returned product: " + item.getProduct().getId() + " quantity: " + item.getQuantity());
        }
        double refundTotal = returnService.processRefund("RET1");
        System.out.println("Refund total: " + refundTotal);

        System.out.println();
        System.out.println("=== RESTOCKING ===");
        RestockOperation restockOperation = restockService.registerRestock("RS1", "P4", "SU1", 10, 40.0);
        System.out.println("Restock entry id: " + restockOperation.getId());
        System.out.println("Restocked product: " + restockOperation.getProduct().getId() + " quantity: " + restockOperation.getQuantity());
        System.out.println("Restock total cost: " + restockOperation.getTotalCost());

        System.out.println();
        System.out.println("=== MANUAL CASHFLOW OUTFLOW ===");
        cashflowService.recordManualOutflow("Office rent payment", 150.0);
        System.out.println("Manual outflow of 150.0 recorded for office rent");

        System.out.println();
        System.out.println("=== CSV EXPORTS ===");
        String inventoryFile = csvExportService.exportInventory("inventory.csv");
        System.out.println("Inventory exported to: " + inventoryFile);
        String cashflowFile = csvExportService.exportCashflow("cashflow.csv");
        System.out.println("Cashflow exported to: " + cashflowFile);

        System.out.println();
        System.out.println("=== FINAL STOCK VALUES ===");
        System.out.println("P1 stock: " + stockService.getStock("P1"));
        System.out.println("P2 stock: " + stockService.getStock("P2"));
        System.out.println("P3 stock: " + stockService.getStock("P3"));
        System.out.println("P4 stock: " + stockService.getStock("P4"));
        System.out.println("P5 stock: " + stockService.getStock("P5"));

        System.out.println();
        System.out.println("=== INVALID OPERATION ATTEMPT ===");
        try {
            stockService.decreaseStock("P3", 10000);
            System.out.println("ERROR: invalid operation was not rejected");
        } catch (StoreOperationException e) {
            System.out.println("Invalid operation correctly rejected: " + e.getMessage());
        }

        try {
            paymentService.registerPayment("PAY2", "ORD2", 10.0);
            System.out.println("ERROR: payment on cancelled order was not rejected");
        } catch (StoreOperationException e) {
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

        System.out.println();
        System.out.println("=== SCENARIO COMPLETE ===");
    }
}