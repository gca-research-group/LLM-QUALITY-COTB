package com.store.main;

import com.store.csv.CsvExportService;
import com.store.exception.InvalidOperationException;
import com.store.model.Cart;
import com.store.model.Category;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.model.Order;
import com.store.model.Product;
import com.store.model.RestockEntry;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.model.Supplier;
import com.store.promotion.BuyXGetYPromotion;
import com.store.promotion.FixedDiscountForTierPromotion;
import com.store.promotion.PercentageThresholdPromotion;
import com.store.report.ReportService;
import com.store.repository.CartRepository;
import com.store.repository.CashflowRepository;
import com.store.repository.CategoryRepository;
import com.store.repository.CustomerRepository;
import com.store.repository.OrderRepository;
import com.store.repository.PaymentRepository;
import com.store.repository.ProductRepository;
import com.store.repository.RestockRepository;
import com.store.repository.ReturnRequestRepository;
import com.store.repository.SupplierRepository;
import com.store.service.CartService;
import com.store.service.CashflowService;
import com.store.service.CatalogService;
import com.store.service.CustomerService;
import com.store.service.OrderService;
import com.store.service.PaymentService;
import com.store.service.PromotionService;
import com.store.service.RestockService;
import com.store.service.ReturnService;
import com.store.service.ShippingService;
import com.store.service.StockService;
import com.store.service.SupplierService;
import com.store.shipping.ExpressShipping;
import com.store.shipping.StandardShipping;

public class Main {

    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        SupplierRepository supplierRepository = new SupplierRepository();
        CartRepository cartRepository = new CartRepository();
        OrderRepository orderRepository = new OrderRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        ReturnRequestRepository returnRequestRepository = new ReturnRequestRepository();
        RestockRepository restockRepository = new RestockRepository();
        CashflowRepository cashflowRepository = new CashflowRepository();

        CatalogService catalogService = new CatalogService(productRepository, categoryRepository);
        StockService stockService = new StockService(catalogService);
        CustomerService customerService = new CustomerService(customerRepository);
        SupplierService supplierService = new SupplierService(supplierRepository);
        CartService cartService = new CartService(cartRepository);
        PromotionService promotionService = new PromotionService();
        ShippingService shippingService = new ShippingService();
        OrderService orderService = new OrderService(orderRepository, stockService);
        CashflowService cashflowService = new CashflowService(cashflowRepository);
        PaymentService paymentService = new PaymentService(paymentRepository, cashflowService);
        ReturnService returnService = new ReturnService(returnRequestRepository, stockService, cashflowService);
        RestockService restockService = new RestockService(restockRepository, stockService, cashflowService);
        ReportService reportService = new ReportService(catalogService, orderService, cashflowService, returnService, restockService);
        CsvExportService csvExportService = new CsvExportService(catalogService, cashflowService);

        System.out.println("=== Setting up categories ===");
        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        catalogService.registerCategory(office);
        catalogService.registerCategory(accessories);
        catalogService.registerCategory(bags);

        System.out.println("=== Setting up products ===");
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

        System.out.println("=== Initializing stock ===");
        stockService.increaseStock("P1", 100);
        stockService.increaseStock("P2", 200);
        stockService.increaseStock("P3", 40);
        stockService.increaseStock("P4", 15);
        stockService.increaseStock("P5", 30);

        System.out.println("=== Registering customers ===");
        Customer cu1 = new Customer("CU1", "Ana", LoyaltyTier.REGULAR);
        Customer cu2 = new Customer("CU2", "Bruno", LoyaltyTier.GOLD);
        Customer cu3 = new Customer("CU3", "Carla", LoyaltyTier.SILVER);
        customerService.registerCustomer(cu1);
        customerService.registerCustomer(cu2);
        customerService.registerCustomer(cu3);

        System.out.println("=== Registering suppliers ===");
        Supplier su1 = new Supplier("SU1", "Alpha Supplies");
        Supplier su2 = new Supplier("SU2", "Beta Wholesale");
        supplierService.registerSupplier(su1);
        supplierService.registerSupplier(su2);

        System.out.println("=== Registering promotions ===");
        promotionService.registerPromotion(new PercentageThresholdPromotion(10.0, 200.0));
        promotionService.registerPromotion(new FixedDiscountForTierPromotion(5.0, 50.0, LoyaltyTier.GOLD));
        promotionService.registerPromotion(new BuyXGetYPromotion("P2", 2, 1));

        System.out.println();
        System.out.println("Number of registered products: " + catalogService.findAllProducts().size());
        System.out.println("Number of registered customers: " + customerService.findAll().size());
        System.out.println("Number of registered suppliers: " + supplierService.findAll().size());

        System.out.println();
        System.out.println("=== Cart for customer CU2 ===");
        Cart cart1 = cartService.createCart("CART1", cu2);
        cartService.addProduct("CART1", p4, 2);
        cartService.addProduct("CART1", p1, 3);
        cartService.addProduct("CART1", p2, 5);
        cartService.addProduct("CART1", p5, 2);
        System.out.println("Cart CART1 subtotal (before changes): " + format(cartService.calculateSubtotal("CART1")));

        cartService.updateQuantity("CART1", p1, 5);
        System.out.println("Cart CART1 subtotal (after quantity update): " + format(cartService.calculateSubtotal("CART1")));

        cartService.removeProduct("CART1", p5);
        System.out.println("Cart CART1 subtotal (after item removal): " + format(cartService.calculateSubtotal("CART1")));

        System.out.println();
        System.out.println("=== Creating order ORD1 from CART1 ===");
        Order order1 = orderService.createOrderFromCart("ORD1", cart1);
        promotionService.applyBestPromotion(order1);
        shippingService.applyShipping(order1, new ExpressShipping(25.0));

        System.out.println("ORD1 subtotal: " + format(order1.getSubtotal()));
        System.out.println("ORD1 promotion applied: " + order1.getAppliedPromotionDescription());
        System.out.println("ORD1 promotion discount: " + format(order1.getPromotionDiscount()));
        System.out.println("ORD1 shipping rule: " + order1.getAppliedShippingDescription());
        System.out.println("ORD1 shipping cost: " + format(order1.getShippingCost()));
        System.out.println("ORD1 final total: " + format(order1.getFinalTotal()));

        paymentService.registerPayment(order1, order1.getFinalTotal());
        System.out.println("ORD1 status after payment: " + order1.getStatus());

        System.out.println();
        System.out.println("=== Cart for customer CU1 ===");
        Cart cart2 = cartService.createCart("CART2", cu1);
        cartService.addProduct("CART2", p3, 2);
        cartService.addProduct("CART2", p4, 1);
        System.out.println("Cart CART2 subtotal: " + format(cartService.calculateSubtotal("CART2")));

        System.out.println();
        System.out.println("=== Creating order ORD2 from CART2 ===");
        Order order2 = orderService.createOrderFromCart("ORD2", cart2);
        promotionService.applyBestPromotion(order2);
        shippingService.applyShipping(order2, new StandardShipping(12.0));

        System.out.println("ORD2 subtotal: " + format(order2.getSubtotal()));
        System.out.println("ORD2 promotion applied: " + order2.getAppliedPromotionDescription());
        System.out.println("ORD2 promotion discount: " + format(order2.getPromotionDiscount()));
        System.out.println("ORD2 shipping rule: " + order2.getAppliedShippingDescription());
        System.out.println("ORD2 shipping cost: " + format(order2.getShippingCost()));
        System.out.println("ORD2 final total: " + format(order2.getFinalTotal()));

        orderService.cancelOrder("ORD2");
        System.out.println("ORD2 status after cancellation: " + order2.getStatus());

        System.out.println();
        System.out.println("=== Attempting invalid operation: paying a cancelled order ===");
        try {
            paymentService.registerPayment(order2, order2.getFinalTotal());
            System.out.println("ERROR: payment for a cancelled order was not rejected");
        } catch (InvalidOperationException e) {
            System.out.println("Invalid operation correctly rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Return request for ORD1 ===");
        ReturnRequest returnRequest = returnService.createReturnRequest(order1);
        returnService.addReturnedItem(returnRequest, p1, 2);
        System.out.println("Return request identifier: " + returnRequest.getId());
        for (ReturnItem item : returnRequest.getItems()) {
            System.out.println("Returned product: " + item.getProduct().getId() + ", quantity: " + item.getQuantity());
        }
        double refundTotal = returnService.processRefund(returnRequest);
        System.out.println("Refund total: " + format(refundTotal));

        System.out.println();
        System.out.println("=== Restocking operation ===");
        RestockEntry restockEntry = restockService.registerRestock(p4, su1, 10, 40.0);
        System.out.println("Restock entry identifier: " + restockEntry.getId());
        System.out.println("Restocked product: " + restockEntry.getProduct().getId() + ", quantity: " + restockEntry.getQuantity());
        System.out.println("Restock total cost: " + format(restockEntry.getTotalCost()));

        System.out.println();
        System.out.println("=== Manual cashflow outflow ===");
        cashflowService.registerManualOutflow(150.0, "Office rent");
        System.out.println("Manual outflow entry of " + format(150.0) + " registered for office rent");

        System.out.println();
        System.out.println("=== Final stock values ===");
        System.out.println("P1 stock: " + stockService.getStock("P1"));
        System.out.println("P2 stock: " + stockService.getStock("P2"));
        System.out.println("P3 stock: " + stockService.getStock("P3"));
        System.out.println("P4 stock: " + stockService.getStock("P4"));
        System.out.println("P5 stock: " + stockService.getStock("P5"));

        System.out.println();
        System.out.println("=== CSV export ===");
        String inventoryFile = csvExportService.exportInventory("inventory.csv");
        System.out.println("Inventory data exported to: " + inventoryFile);
        String cashflowFile = csvExportService.exportCashflow("cashflow.csv");
        System.out.println("Cashflow data exported to: " + cashflowFile);

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

    private static String format(double value) {
        return String.format("%.2f", value);
    }
}