package store.main;

import store.csv.CsvExporter;
import store.exception.BusinessRuleException;
import store.model.Cart;
import store.model.Category;
import store.model.Customer;
import store.model.LoyaltyTier;
import store.model.Order;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.model.Supplier;
import store.promotion.BuyXGetYPromotion;
import store.promotion.FixedDiscountPromotion;
import store.promotion.PercentageThresholdPromotion;
import store.repository.CartRepository;
import store.repository.CashflowRepository;
import store.repository.CategoryRepository;
import store.repository.CustomerRepository;
import store.repository.OrderRepository;
import store.repository.PaymentRepository;
import store.repository.ProductRepository;
import store.repository.RestockRepository;
import store.repository.ReturnRequestRepository;
import store.repository.SupplierRepository;
import store.service.CartService;
import store.service.CashflowService;
import store.service.CatalogService;
import store.service.CustomerService;
import store.service.OrderService;
import store.service.PaymentService;
import store.service.PromotionService;
import store.service.ReportService;
import store.service.RestockService;
import store.service.ReturnService;
import store.service.ShippingService;
import store.service.SupplierService;
import store.shipping.ExpressShippingRule;
import store.shipping.PickupShippingRule;
import store.shipping.ShippingType;
import store.shipping.StandardShippingRule;

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
        CustomerService customerService = new CustomerService(customerRepository);
        SupplierService supplierService = new SupplierService(supplierRepository);
        CartService cartService = new CartService(cartRepository, catalogService, customerService);
        PromotionService promotionService = new PromotionService();
        ShippingService shippingService = new ShippingService();
        OrderService orderService = new OrderService(orderRepository, catalogService, promotionService, shippingService);
        CashflowService cashflowService = new CashflowService(cashflowRepository);
        PaymentService paymentService = new PaymentService(paymentRepository, orderService, cashflowService);
        ReturnService returnService = new ReturnService(returnRequestRepository, orderService, catalogService, cashflowService);
        RestockService restockService = new RestockService(restockRepository, catalogService, supplierService, cashflowService);
        ReportService reportService = new ReportService(catalogService, orderService, cashflowService, returnService, restockService);
        CsvExporter csvExporter = new CsvExporter();

        System.out.println("=== Store Management System Scenario ===");

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

        catalogService.increaseStock("P1", 100);
        catalogService.increaseStock("P2", 200);
        catalogService.increaseStock("P3", 40);
        catalogService.increaseStock("P4", 15);
        catalogService.increaseStock("P5", 30);

        customerService.registerCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        customerService.registerCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        customerService.registerCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));

        supplierService.registerSupplier(new Supplier("SU1", "Alpha Supplies"));
        supplierService.registerSupplier(new Supplier("SU2", "Beta Wholesale"));

        promotionService.registerPromotion(new PercentageThresholdPromotion("PROMO1", 0.10, 200.0));
        promotionService.registerPromotion(new FixedDiscountPromotion("PROMO2", 5.0, 50.0, LoyaltyTier.GOLD));
        promotionService.registerPromotion(new BuyXGetYPromotion("PROMO3", "P2", 2, 1));

        shippingService.registerRule(new StandardShippingRule(12.0));
        shippingService.registerRule(new ExpressShippingRule(25.0));
        shippingService.registerRule(new PickupShippingRule());

        System.out.println("Number of registered products: " + catalogService.totalProductCount());
        System.out.println("Number of registered customers: " + customerService.totalCustomerCount());
        System.out.println("Number of registered suppliers: " + supplierService.totalSupplierCount());

        Cart cart1 = cartService.createCart("CU2");
        cartService.addProduct(cart1.getId(), "P4", 1);
        cartService.addProduct(cart1.getId(), "P1", 3);
        cartService.addProduct(cart1.getId(), "P2", 5);
        cartService.addProduct(cart1.getId(), "P5", 2);
        System.out.println("Cart " + cart1.getId() + " subtotal before quantity/item changes: " + cartService.getSubtotal(cart1.getId()));

        cartService.updateQuantity(cart1.getId(), "P2", 8);
        System.out.println("Cart " + cart1.getId() + " subtotal after quantity change: " + cartService.getSubtotal(cart1.getId()));

        cartService.removeProduct(cart1.getId(), "P5");
        System.out.println("Cart " + cart1.getId() + " subtotal after item removal: " + cartService.getSubtotal(cart1.getId()));

        Order order1 = orderService.createOrderFromCart("ORD1", cart1);
        System.out.println("Order ORD1 subtotal: " + order1.getSubtotal());

        double order1Discount = orderService.applyPromotion("ORD1", "PROMO2");
        System.out.println("Order ORD1 promotion discount: " + order1Discount);

        double order1Shipping = orderService.applyShipping("ORD1", ShippingType.STANDARD);
        System.out.println("Order ORD1 shipping cost: " + order1Shipping);

        double order1FinalTotal = orderService.calculateFinalTotal("ORD1");
        System.out.println("Order ORD1 final total: " + order1FinalTotal);

        paymentService.registerPayment("ORD1", order1FinalTotal, "CREDIT_CARD");
        System.out.println("Order ORD1 final status: " + orderService.findOrder("ORD1").getStatus());

        Cart cart2 = cartService.createCart("CU1");
        cartService.addProduct(cart2.getId(), "P3", 2);
        cartService.addProduct(cart2.getId(), "P5", 1);

        Order order2 = orderService.createOrderFromCart("ORD2", cart2);
        System.out.println("Order ORD2 subtotal: " + order2.getSubtotal());
        System.out.println("Order ORD2 promotion discount: " + order2.getPromotionDiscount());

        double order2Shipping = orderService.applyShipping("ORD2", ShippingType.EXPRESS);
        System.out.println("Order ORD2 shipping cost: " + order2Shipping);

        double order2FinalTotal = orderService.calculateFinalTotal("ORD2");
        System.out.println("Order ORD2 final total: " + order2FinalTotal);

        orderService.cancelOrder("ORD2");
        System.out.println("Order ORD2 final status: " + orderService.findOrder("ORD2").getStatus());

        ReturnRequest returnRequest = returnService.createReturnRequest("ORD1");
        System.out.println("Return request identifier: " + returnRequest.getId());

        String returnedProductA = "P1";
        int returnedQuantityA = 1;
        String returnedProductB = "P2";
        int returnedQuantityB = 2;
        returnService.addReturnedItem(returnRequest.getId(), returnedProductA, returnedQuantityA);
        returnService.addReturnedItem(returnRequest.getId(), returnedProductB, returnedQuantityB);
        System.out.println("Returned product " + returnedProductA + " quantity: " + returnedQuantityA);
        System.out.println("Returned product " + returnedProductB + " quantity: " + returnedQuantityB);

        double refundTotal = returnService.calculateRefundTotal(returnRequest.getId());
        System.out.println("Refund total: " + refundTotal);

        double processedRefund = returnService.processRefund(returnRequest.getId());
        System.out.println("Refund processed for return " + returnRequest.getId() + ": " + processedRefund);

        RestockEntry restockEntry = restockService.registerRestock("P4", "SU1", 10, 50.0);
        System.out.println("Restock entry identifier: " + restockEntry.getId());
        System.out.println("Restocked product identifier: " + restockEntry.getProduct().getId() + ", quantity: " + restockEntry.getQuantity());
        System.out.println("Restock total cost: " + restockEntry.getTotalCost());

        cashflowService.recordOutflow(100.0, "Manual operational expense - store rent");
        System.out.println("Manual cashflow outflow entry registered: 100.0");

        String inventoryFile = csvExporter.exportInventory(catalogService.listAllProducts(), "inventory.csv");
        System.out.println("Inventory exported to: " + inventoryFile);

        String cashflowFile = csvExporter.exportCashflow(cashflowService.findAllEntries(), "cashflow.csv");
        System.out.println("Cashflow exported to: " + cashflowFile);

        try {
            paymentService.registerPayment("ORD2", 90.0, "CREDIT_CARD");
            System.out.println("ERROR: invalid payment was not rejected");
        } catch (BusinessRuleException ex) {
            System.out.println("Invalid operation rejected as expected: " + ex.getMessage());
        }

        System.out.println("Final stock P1: " + catalogService.findProduct("P1").getStockQuantity());
        System.out.println("Final stock P2: " + catalogService.findProduct("P2").getStockQuantity());
        System.out.println("Final stock P3: " + catalogService.findProduct("P3").getStockQuantity());
        System.out.println("Final stock P4: " + catalogService.findProduct("P4").getStockQuantity());
        System.out.println("Final stock P5: " + catalogService.findProduct("P5").getStockQuantity());

        reportService.printStockReport();
        reportService.printSalesByProductReport();
        reportService.printOrdersByStatusReport();
        reportService.printCashflowSummaryReport();
        reportService.printReturnsRefundsReport();
        reportService.printRestockingReport();
    }
}