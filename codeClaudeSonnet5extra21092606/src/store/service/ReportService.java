package store.service;

import store.model.Order;
import store.model.OrderItem;
import store.model.OrderStatus;
import store.model.Product;
import store.model.RestockEntry;
import store.model.ReturnRequest;
import store.repository.OrderRepository;
import store.repository.ProductRepository;
import store.repository.RestockEntryRepository;
import store.repository.ReturnRequestRepository;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CashflowService cashflowService;
    private final ReturnRequestRepository returnRequestRepository;
    private final RestockEntryRepository restockEntryRepository;

    public ReportService(ProductRepository productRepository,
                          OrderRepository orderRepository,
                          CashflowService cashflowService,
                          ReturnRequestRepository returnRequestRepository,
                          RestockEntryRepository restockEntryRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.cashflowService = cashflowService;
        this.returnRequestRepository = returnRequestRepository;
        this.restockEntryRepository = restockEntryRepository;
    }

    public Map<String, Integer> stockReport() {
        Map<String, Integer> report = new LinkedHashMap<>();
        for (Product product : productRepository.findAll()) {
            report.put(product.getId(), product.getStockQuantity());
        }
        return report;
    }

    public Map<String, Integer> salesByProductReport() {
        Map<String, Integer> report = new LinkedHashMap<>();
        for (Product product : productRepository.findAll()) {
            report.put(product.getId(), 0);
        }
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REFUNDED) {
                for (OrderItem item : order.getItems()) {
                    String productId = item.getProduct().getId();
                    report.put(productId, report.getOrDefault(productId, 0) + item.getQuantity());
                }
            }
        }
        return report;
    }

    public Map<OrderStatus, Integer> ordersByStatusReport() {
        Map<OrderStatus, Integer> report = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            report.put(status, 0);
        }
        for (Order order : orderRepository.findAll()) {
            report.put(order.getStatus(), report.get(order.getStatus()) + 1);
        }
        return report;
    }

    public double totalInflow() {
        return cashflowService.totalInflow();
    }

    public double totalOutflow() {
        return cashflowService.totalOutflow();
    }

    public double netCashflow() {
        return cashflowService.netCashflow();
    }

    public List<ReturnRequest> returnsRefundsReport() {
        return new ArrayList<>(returnRequestRepository.findAll());
    }

    public List<RestockEntry> restockingReport() {
        return new ArrayList<>(restockEntryRepository.findAll());
    }
}