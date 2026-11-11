package com.store.main;

import com.store.csv.CsvExportService;
import com.store.exception.DuplicateIdException;
import com.store.exception.InactiveProductException;
import com.store.exception.InvalidOrderStateException;
import com.store.model.Cart;
import com.store.model.Category;
import com.store.model.Customer;
import com.store.model.LoyaltyTier;
import com.store.model.Order;
import com.store.model.Product;
import com.store.model.RestockOperation;
import com.store.model.ReturnItem;
import com.store.model.ReturnRequest;
import com.store.model.Supplier;
import com.store.promotion.BuyXGetYFreePromotion;
import com.store.promotion.FixedDiscountPromotion;
import com.store.promotion.PercentageThresholdPromotion;
import com.store.repository.CartRepository;
import com.store.repository.CashflowRepository;
import com.store.repository.CategoryRepository;
import com.store.repository.CustomerRepository;
import com.store.repository.OrderRepository;
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
import com.store.service.ReportService;
import com.store.service.RestockService;
import com.store.service.ReturnService;
import com.store.service.ShippingService;
import com.store.service.StockService;
import com.store.service.SupplierService;
import com.store.shipping.ExpressShipping;
import com.store.shipping.PickupShipping;
import com.store.shipping.StandardShipping;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CategoryRepository categoryRepository = new CategoryRepository();
        ProductRepository productRepository = new ProductRepository();
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
        CartService cartService = new CartService(cartRepository, productRepository);
        OrderService orderService = new OrderService(orderRepository, stockService);
        PromotionService promotionService = new PromotionService();
        ShippingService shippingService = new ShippingService();
        CashflowService cashflowService = new CashflowService(cashflowRepository);
        PaymentService paymentService = new PaymentService(orderRepository, cashflowService);
        ReturnService returnService = new ReturnService(orderRepository, returnRequestRepository, productRepository, stockService, cashflowService);
        RestockService restockService = new RestockService(productRepository, supplierRepository, restockRepository, stockService, cashflowService);
        ReportService reportService = new ReportService(productRepository, orderRepository, cashflowService, returnRequestRepository, restockRepository);
        CsvExportService csvExportService = new CsvExportService();

        System.out.println("==== STORE MANAGEMENT SYSTEM DEMO ====");

        Category office = new Category("C1", "Office");
        Category accessories = new Category("C2", "Accessories");
        Category bags = new Category("C3", "Bags");
        catalogService.addCategory(office);
        catalogService.addCategory(accessories);
        catalogService.addCategory(bags);

        catalogService.addProduct(new Product("P1", "Notebook", 15.0, office));
        catalogService.addProduct(new Product("P2", "Pen", 5.0, office));
        catalogService.addProduct(new Product("P3", "Mug", 20.0, accessories));
        catalogService.addProduct(new Product("P4", "Backpack", 80.0, bags));
        catalogService.addProduct(new Product("P5", "Bottle", 25.0, accessories));

        stockService.increaseStock("P1", 100);
        stockService.increaseStock("P2", 200);
        stockService.increaseStock("P3", 40);
        stockService.increaseStock("P4", 15);
        stockService.increaseStock("P5", 30);

        customerService.registerCustomer(new Customer("CU1", "Ana", LoyaltyTier.REGULAR));
        customerService.registerCustomer(new Customer("CU2", "Bruno", LoyaltyTier.GOLD));
        customerService.registerCustomer(new Customer("CU3", "Carla", LoyaltyTier.SILVER));

        supplierService.registerSupplier(new Supplier("SU1", "Alpha Supplies"));
        supplierService.registerSupplier(new Supplier("SU2", "Beta Wholesale"));

        promotionService.register(new PercentageThresholdPromotion("PROMO_PCT10", 0.10, 200.0));
        promotionService.register(new FixedDiscountPromotion("PROMO_GOLD5", 5.0, 50.0, LoyaltyTier.GOLD));
        promotionService.register(new BuyXGetYFreePromotion("PROMO_P2_B2G1", "P2", 2, 1));

        shippingService.register(new StandardShipping(12.0));
        shippingService.register(new ExpressShipping(25.0));
        shippingService.register(new PickupShipping());

        System.out.println("Registered products: " + catalogService.countProducts());
        System.out.println("Registered customers: " + customerService.countCustomers());
        System.out.println("Registered suppliers: " + supplierService.countSuppliers());

        Product foundById = catalogService.findById("P2");
        System.out.println("Product lookup by id P2: " + foundById.getName());

        List<Product> byName = catalogService.findByName("Pen");
        System.out.println("Product lookup by name 'Pen' found: " + byName.size());

        List<Product> byCategory = catalogService.listByCategory("C2");
        System.out.println("Products in category Accessories: " + byCategory.size());

        Cart cart1 = cartService.createCart("CART1", "CU2");
        cartService.addItemToCart("CART1", "P4", 1);
        cartService.addItemToCart("CART1", "P1", 2);
        cartService.addItemToCart("CART1", "P2", 10);
        cartService.addItemToCart("CART1", "P5", 1);
        System.out.println("Cart1 subtotal before changes: " + fmt(cartService.getCartSubtotal("CART1")));

        cartService.updateCartItemQuantity("CART1", "P2", 4);
        System.out.println("Cart1 subtotal after quantity change: " + fmt(cartService.getCartSubtotal("CART1")));

        cartService.removeCartItem("CART1", "P5");
        System.out.println("Cart1 subtotal after item removal: " + fmt(cartService.getCartSubtotal("CART1")));

        Order ord1 = orderService.createOrderFromCart("ORD1", cart1);
        System.out.println("ORD1 subtotal: " + fmt(ord1.getSubtotal()));

        Customer bruno = customerService.findById("CU2");
        orderService.applyPromotion("ORD1", promotionService.getByName("PROMO_GOLD5"), bruno);
        System.out.println("ORD1 promotion discount: " + fmt(ord1.getPromotionDiscount()));

        orderService.applyShipping("ORD1", shippingService.getByName("EXPRESS"));
        System.out.println("ORD1 shipping cost: " + fmt(ord1.getShippingCost()));

        System.out.println("ORD1 final total: " + fmt(ord1.getFinalTotal()));

        paymentService.registerPayment("PAY1", "ORD1", ord1.getFinalTotal());
        System.out.println("ORD1 status: " + ord1.getStatus());

        Cart cart2 = cartService.createCart("CART2", "CU1");
        cartService.addItemToCart("CART2", "P3", 2);
        cartService.addItemToCart("CART2", "P1", 1);

        Order ord2 = orderService.createOrderFromCart("ORD2", cart2);
        System.out.println("ORD2 subtotal: " + fmt(ord2.getSubtotal()));

        orderService.applyShipping("ORD2", shippingService.getByName("STANDARD"));
        System.out.println("ORD2 promotion discount: " + fmt(ord2.getPromotionDiscount()));
        System.out.println("ORD2 shipping cost: " + fmt(ord2.getShippingCost()));
        System.out.println("ORD2 final total: " + fmt(ord2.getFinalTotal()));

        orderService.cancelOrder("ORD2");
        System.out.println("ORD2 status: " + ord2.getStatus());

        ReturnRequest returnRequest = returnService.createReturnRequest("RET1", "ORD1");
        returnService.addReturnItem("RET1", "P4", 1);
        double previewRefund = returnService.calculateRefundTotal("RET1");
        System.out.println("Return request id: " + returnRequest.getId());
        for (ReturnItem item : returnRequest.getItems()) {
            System.out.println("Returned product: " + item.getProductId() + " quantity: " + item.getQuantity());
        }
        System.out.println("Calculated refund total: " + fmt(previewRefund));

        double processedRefund = returnService.processRefund("RET1");
        System.out.println("Processed refund total: " + fmt(processedRefund));

        RestockOperation restock1 = restockService.registerRestock("RESTOCK1", "P4", "SU1", 10, 50.0);
        System.out.println("Restock id: " + restock1.getId());
        System.out.println("Restocked product: " + restock1.getProductId() + " quantity: " + restock1.getQuantity());
        System.out.println("Restock total cost: " + fmt(restock1.getTotalCost()));

        cashflowService.addManualOutflow(300.0, "Office rent");
        System.out.println("Manual cashflow outflow registered: " + fmt(300.0));

        try {
            String inventoryFile = csvExportService.exportInventory("inventory.csv", productRepository.findAll());
            System.out.println("Inventory exported to: " + inventoryFile);
        } catch (IOException e) {
            System.out.println("Failed to export inventory: " + e.getMessage());
        }

        try {
            String cashflowFile = csvExportService.exportCashflow("cashflow.csv", cashflowService.getAllEntries());
            System.out.println("Cashflow exported to: " + cashflowFile);
        } catch (IOException e) {
            System.out.println("Failed to export cashflow: " + e.getMessage());
        }

        try {
            paymentService.registerPayment("PAY2", "ORD2", ord2.getFinalTotal());
            System.out.println("ERROR: payment for cancelled order should have been rejected");
        } catch (InvalidOrderStateException e) {
            System.out.println("Rejected invalid operation - pay cancelled order: " + e.getMessage());
        }

        try {
            catalogService.addProduct(new Product("P1", "Duplicate Notebook", 10.0, office));
            System.out.println("ERROR: duplicate product id should have been rejected");
        } catch (DuplicateIdException e) {
            System.out.println("Rejected invalid operation - duplicate product id: " + e.getMessage());
        }

        catalogService.deactivateProduct("P5");
        try {
            cartService.addItemToCart("CART2", "P5", 1);
            System.out.println("ERROR: inactive product should have been rejected");
        } catch (InactiveProductException e) {
            System.out.println("Rejected invalid operation - inactive product in cart: " + e.getMessage());
        }

        System.out.println("Final stock P1: " + stockService.getStock("P1"));
        System.out.println("Final stock P2: " + stockService.getStock("P2"));
        System.out.println("Final stock P3: " + stockService.getStock("P3"));
        System.out.println("Final stock P4: " + stockService.getStock("P4"));
        System.out.println("Final stock P5: " + stockService.getStock("P5"));

        System.out.println("---- STOCK REPORT ----");
        reportService.printStockReport();

        System.out.println("---- SALES BY PRODUCT REPORT ----");
        reportService.printSalesByProductReport();

        System.out.println("---- ORDERS BY STATUS REPORT ----");
        reportService.printOrdersByStatusReport();

        System.out.println("---- CASHFLOW SUMMARY REPORT ----");
        reportService.printCashflowSummaryReport();

        System.out.println("---- RETURNS/REFUNDS REPORT ----");
        reportService.printReturnsRefundsReport();

        System.out.println("---- RESTOCKING REPORT ----");
        reportService.printRestockingReport();

        System.out.println("==== END OF DEMO ====");
    }

    private static String fmt(double value) {
        return String.format("%.2f", value);
    }
}