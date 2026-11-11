package com.store.core;

import com.store.cart.CartService;
import com.store.cashflow.CashflowService;
import com.store.catalog.Catalog;
import com.store.catalog.StockService;
import com.store.csv.CsvExporter;
import com.store.order.OrderService;
import com.store.payment.PaymentService;
import com.store.people.CustomerRepository;
import com.store.people.SupplierRepository;
import com.store.promotion.PromotionService;
import com.store.report.ReportService;
import com.store.restock.RestockService;
import com.store.returns.ReturnService;
import com.store.shipping.ShippingService;

public class Store {
    private final Catalog catalog;
    private final StockService stockService;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final CartService cartService;
    private final OrderService orderService;
    private final PromotionService promotionService;
    private final ShippingService shippingService;
    private final CashflowService cashflowService;
    private final PaymentService paymentService;
    private final ReturnService returnService;
    private final RestockService restockService;
    private final ReportService reportService;
    private final CsvExporter csvExporter;

    public Store() {
        this.catalog = new Catalog();
        this.stockService = new StockService(catalog);
        this.customerRepository = new CustomerRepository();
        this.supplierRepository = new SupplierRepository();
        this.cartService = new CartService(catalog, customerRepository);
        this.orderService = new OrderService(stockService);
        this.promotionService = new PromotionService();
        this.shippingService = new ShippingService();
        this.cashflowService = new CashflowService();
        this.paymentService = new PaymentService(orderService, cashflowService);
        this.returnService = new ReturnService(orderService, stockService, cashflowService);
        this.restockService = new RestockService(stockService, supplierRepository, cashflowService);
        this.reportService = new ReportService(catalog, orderService, cashflowService, returnService, restockService);
        this.csvExporter = new CsvExporter(catalog, cashflowService);
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public StockService getStockService() {
        return stockService;
    }

    public CustomerRepository getCustomerRepository() {
        return customerRepository;
    }

    public SupplierRepository getSupplierRepository() {
        return supplierRepository;
    }

    public CartService getCartService() {
        return cartService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public PromotionService getPromotionService() {
        return promotionService;
    }

    public ShippingService getShippingService() {
        return shippingService;
    }

    public CashflowService getCashflowService() {
        return cashflowService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public ReturnService getReturnService() {
        return returnService;
    }

    public RestockService getRestockService() {
        return restockService;
    }

    public ReportService getReportService() {
        return reportService;
    }

    public CsvExporter getCsvExporter() {
        return csvExporter;
    }
}