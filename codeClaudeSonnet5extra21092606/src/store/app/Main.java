package store.app;

import store.exception.DuplicateIdException;
import store.exception.InactiveProductException;
import store.exception.InsufficientStockException;
import store.exception.InvalidOperationException;
import store.model.Cart;
import store.model.CashflowEntry;
import store.model.Category;
import store.model.Customer;
import store.model.LoyaltyTier;
import store.model.Order;
import store.model.OrderStatus;
import store.model.Payment;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnItem;
import store.model.ReturnRequest;
import store.model.Supplier;
import store.promotion.BuyXGetYPromotion;
import store.promotion.FixedDiscountPromotion;
import store.promotion.PercentageThresholdPromotion;
import store.promotion.Promotion;
import store.repository.CartRepository;
import store.repository.CashflowRepository;
import store.repository.CategoryRepository;
import store.repository.CustomerRepository;
import store.repository.OrderRepository;
import store.repository.PaymentRepository;
import store.repository.ProductRepository;
import store.repository.RestockEntryRepository;
import store.repository.ReturnRequestRepository;
import store.repository.SupplierRepository;
import store.service.CartService;
import store.service.CashflowService;
import store.service.CatalogService;
import store.service.CsvExportService;
import store.service.CustomerService;
import store.service.OrderService;
import store.service.PaymentService;
import store.service.ReportService;
import store.service.RestockService;
import store.service.ReturnService;
import store.service.StockService;
import store.service.SupplierService;
import store.shipping.ExpressShipping;
import store.shipping.PickupShipping;
import store.shipping.ShippingRule;
import store.shipping.StandardShipping;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        CategoryRepository categoryRepository = new CategoryRepository();
        ProductRepository productRepository = new ProductRepository();
        CustomerRepository customerRepository = new CustomerRepository();
        SupplierRepository supplierRepository = new SupplierRepository();
        CartRepository cartRepository = new CartRepository();
        OrderRepository orderRepository = new OrderRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        ReturnRequestRepository returnRequestRepository = new ReturnRequestRepository();
        RestockEntryRepository restockEntryRepository = new RestockEntryRepository();
        CashflowRepository cashflowRepository = new CashflowRepository();

        CatalogService catalogService = new CatalogService(productRepository, categoryRepository);
        StockService stockService = new StockService(productRepository);
        CustomerService customerService = new CustomerService(customerRepository);
        SupplierService supplierService = new SupplierService(supplierRepository);
        CartService cartService = new CartService(cartRepository);
        OrderService orderService = new OrderService(orderRepository, stockService);
        CashflowService cashflowService = new CashflowService(cashflowRepository);
        PaymentService paymentService = new PaymentService(paymentRepository, cashflowService);
        ReturnService returnService = new ReturnService(returnRequestRepository, stockService, cashflowService);
        RestockService restockService = new RestockService(restockEntryRepository, stockService, cashflowService);
        ReportService reportService = new ReportService(productRepository, orderRepository, cashflowService, returnRequestRepository, restockEntryRepository);
        CsvExportService csvExportService = new CsvExportService(productRepository, cashflowRepository);

        System.out.println("=== STORE MANAGEMENT SYSTEM - FIXED SCENARIO ===");

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

        catalogService.registerProduct(notebook);
        catalogService.registerProduct(pen);
        catalogService.registerProduct(mug);
        catalogService.registerProduct(backpack);
        catalogService.registerProduct(bottle);

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

        Promotion tenPercentPromo = new PercentageThresholdPromotion(
                "PROMO1", "10 percent off orders with subtotal >= 200.0", 0.10, 200.0);
        Promotion goldFixedPromo = new FixedDiscountPromotion(
                "PROMO2", "5.0 fixed discount for GOLD customers with subtotal >= 50.0", 5.0, 50.0, LoyaltyTier.GOLD);
        Promotion buy2Get1PenPromo = new BuyXGetYPromotion(
                "PROMO3", "Buy 2 get 1 free on Pen (P2)", "P2", 2, 1);

        ShippingRule standardShipping = new StandardShipping(12.0);
        ShippingRule expressShipping = new ExpressShipping(25.0);
        ShippingRule pickupShipping = new PickupShipping();

        System.out.println();
        System.out.println("Number of registered products: " + catalogService.productCount());
        System.out.println("Number of registered customers: " + customerService.customerCount());
        System.out.println("Number of registered suppliers: " + supplierService.supplierCount());
        System.out.println("Pickup shipping name (for reference): " + pickupShipping.getName());

        System.out.println();
        System.out.println("--- Cart CART1 for customer CU2 (Bruno) ---");
        Cart cart1 = cartService.createCart("CART1", bruno);
        cartService.addProduct("CART1", backpack, 1);
        cartService.addProduct("CART1", notebook, 3);
        cartService.addProduct("CART1", pen, 5);
        cartService.addProduct("CART1", bottle, 2);
        System.out.println("Cart CART1 subtotal before changes: " + cartService.getSubtotal("CART1"));

        cartService.updateQuantity("CART1", "P1", 5);
        System.out.println("Cart CART1 subtotal after quantity change: " + cartService.getSubtotal("CART1"));

        cartService.removeProduct("CART1", "P5");
        System.out.println("Cart CART1 subtotal after item removal: " + cartService.getSubtotal("CART1"));

        System.out.println();
        System.out.println("--- Order ORD1 ---");
        Order ord1 = orderService.createOrderFromCart("ORD1", cart1);
        System.out.println("ORD1 subtotal: " + ord1.getSubtotal());
        System.out.println("Checking promotion applicability for ORD1:");
        System.out.println("  " + tenPercentPromo.getDescription() + " -> applicable: " + tenPercentPromo.isApplicable(ord1));
        System.out.println("  " + goldFixedPromo.getDescription() + " -> applicable: " + goldFixedPromo.isApplicable(ord1));
        System.out.println("  " + buy2Get1PenPromo.getDescription() + " -> applicable: " + buy2Get1PenPromo.isApplicable(ord1));
        boolean promoAppliedOrd1 = orderService.applyPromotion("ORD1", buy2Get1PenPromo);
        System.out.println("Promotion applied to ORD1: " + promoAppliedOrd1 + " (" + buy2Get1PenPromo.getDescription() + ")");
        orderService.applyShipping("ORD1", standardShipping);
        System.out.println("ORD1 promotion discount: " + ord1.getPromotionDiscount());
        System.out.println("ORD1 shipping cost: " + ord1.getShippingCost());
        System.out.println("ORD1 final total: " + ord1.getFinalTotal());

        Payment payment1 = paymentService.registerPayment(ord1, ord1.getFinalTotal());
        System.out.println("Payment registered for ORD1: " + payment1.getId() + ", amount: " + payment1.getAmount());
        System.out.println("ORD1 status after payment: " + ord1.getStatus());

        System.out.println();
        System.out.println("--- Invalid Operation Attempt: cancel a paid order ---");
        try {
            orderService.cancelOrder("ORD1");
            System.out.println("ERROR: cancellation of a paid order was not rejected!");
        } catch (InvalidOperationException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("--- Cart CART2 for customer CU1 (Ana) ---");
        Cart cart2 = cartService.createCart("CART2", ana);
        cartService.addProduct("CART2", mug, 2);
        cartService.addProduct("CART2", pen, 4);
        System.out.println("Cart CART2 subtotal: " + cartService.getSubtotal("CART2"));

        System.out.println();
        System.out.println("--- Order ORD2 ---");
        Order ord2 = orderService.createOrderFromCart("ORD2", cart2);
        orderService.applyShipping("ORD2", expressShipping);
        System.out.println("ORD2 subtotal: " + ord2.getSubtotal());
        System.out.println("ORD2 promotion discount: " + ord2.getPromotionDiscount());
        System.out.println("ORD2 shipping cost: " + ord2.getShippingCost());
        System.out.println("ORD2 final total: " + ord2.getFinalTotal());

        orderService.cancelOrder("ORD2");
        System.out.println("ORD2 status after cancellation: " + ord2.getStatus());

        System.out.println();
        System.out.println("--- Return for ORD1 ---");
        ReturnRequest returnRequest = returnService.createReturnRequest(ord1);
        returnService.addReturnedItem(returnRequest, notebook, 2);
        System.out.println("Return request id: " + returnRequest.getId());
        for (ReturnItem item : returnRequest.getItems()) {
            System.out.println("Returned product: " + item.getProduct().getId() + ", quantity: " + item.getQuantity());
        }
        double refundPreview = returnService.calculateRefundTotal(returnRequest);
        System.out.println("Calculated refund total (preview): " + refundPreview);
        double processedRefund = returnService.processRefund(returnRequest);
        System.out.println("Processed refund total: " + processedRefund);
        System.out.println("ORD1 status after refund: " + ord1.getStatus());

        System.out.println();
        System.out.println("--- Restocking ---");
        RestockEntry restockEntry = restockService.registerRestock(backpack, alpha, 10, 50.0);
        System.out.println("Restock entry id: " + restockEntry.getId());
        System.out.println("Restocked product: " + restockEntry.getProduct().getId() + ", quantity: " + restockEntry.getQuantity());
        System.out.println("Restock total cost: " + restockEntry.getTotalCost());

        System.out.println();
        System.out.println("--- Manual Cashflow Entry ---");
        CashflowEntry manualOutflow = cashflowService.recordOutflow(150.0, "Monthly warehouse rent payment");
        System.out.println("Manual outflow entry id: " + manualOutflow.getId() + ", amount: " + manualOutflow.getAmount());

        System.out.println();
        System.out.println("--- Final Stock Values ---");
        System.out.println("P1 stock: " + stockService.getStock("P1"));
        System.out.println("P2 stock: " + stockService.getStock("P2"));
        System.out.println("P3 stock: " + stockService.getStock("P3"));
        System.out.println("P4 stock: " + stockService.getStock("P4"));
        System.out.println("P5 stock: " + stockService.getStock("P5"));

        System.out.println();
        System.out.println("--- Additional Invalid Operations (must be rejected) ---");

        try {
            catalogService.registerProduct(new Product("P1", "Duplicate Notebook", 10.0, office));
            System.out.println("ERROR: duplicate product id was not rejected!");
        } catch (DuplicateIdException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        try {
            stockService.decreaseStock("P3", 10000);
            System.out.println("ERROR: negative stock operation was not rejected!");
        } catch (InsufficientStockException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        try {
            paymentService.registerPayment(ord2, 100.0);
            System.out.println("ERROR: payment for a cancelled order was not rejected!");
        } catch (InvalidOperationException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        }

        catalogService.deactivateProduct("P5");
        try {
            cartService.addProduct("CART2", bottle, 1);
            System.out.println("ERROR: adding an inactive product to a cart was not rejected!");
        } catch (InactiveProductException e) {
            System.out.println("Rejected as expected: " + e.getMessage());
        } finally {
            catalogService.activateProduct("P5");
        }

        System.out.println();
        System.out.println("--- CSV Export ---");
        String inventoryFile = csvExportService.exportInventory("inventory.csv");
        System.out.println("Inventory data exported to: " + inventoryFile);
        String cashflowFile = csvExportService.exportCashflow("cashflow.csv");
        System.out.println("Cashflow data exported to: " + cashflowFile);

        System.out.println();
        System.out.println("=== STOCK REPORT ===");
        Map<String, Integer> stockReport = reportService.stockReport();
        for (Map.Entry<String, Integer> entry : stockReport.entrySet()) {
            System.out.println("Product " + entry.getKey() + ": " + entry.getValue() + " units in stock");
        }

        System.out.println();
        System.out.println("=== SALES BY PRODUCT REPORT ===");
        Map<String, Integer> salesReport = reportService.salesByProductReport();
        for (Map.Entry<String, Integer> entry : salesReport.entrySet()) {
            System.out.println("Product " + entry.getKey() + ": " + entry.getValue() + " units sold");
        }

        System.out.println();
        System.out.println("=== ORDERS BY STATUS REPORT ===");
        Map<OrderStatus, Integer> statusReport = reportService.ordersByStatusReport();
        for (Map.Entry<OrderStatus, Integer> entry : statusReport.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " order(s)");
        }

        System.out.println();
        System.out.println("=== CASHFLOW SUMMARY REPORT ===");
        System.out.println("Total inflow: " + reportService.totalInflow());
        System.out.println("Total outflow: " + reportService.totalOutflow());
        System.out.println("Net cashflow: " + reportService.netCashflow());

        System.out.println();
        System.out.println("=== RETURNS / REFUNDS REPORT ===");
        List<ReturnRequest> returnRequests = reportService.returnsRefundsReport();
        for (ReturnRequest rr : returnRequests) {
            System.out.println("Return " + rr.getId() + " (order " + rr.getOrder().getId() + "): refund total = " + rr.getRefundTotal());
            for (ReturnItem item : rr.getItems()) {
                System.out.println("   Product " + item.getProduct().getId() + ": " + item.getQuantity() + " unit(s) returned");
            }
        }

        System.out.println();
        System.out.println("=== RESTOCKING REPORT ===");
        List<RestockEntry> restockEntries = reportService.restockingReport();
        for (RestockEntry entry : restockEntries) {
            System.out.println("Restock " + entry.getId() + ": product " + entry.getProduct().getId()
                    + ", supplier " + entry.getSupplier().getId()
                    + ", quantity " + entry.getQuantity()
                    + ", total cost " + entry.getTotalCost());
        }

        System.out.println();
        System.out.println("=== END OF SCENARIO ===");
    }
}