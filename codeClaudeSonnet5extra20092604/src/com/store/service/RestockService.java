package com.store.service;

import com.store.model.Product;
import com.store.model.Supplier;
import com.store.repository.RestockRepository;
import com.store.restock.RestockOperation;

import java.util.List;

public class RestockService {
    private final RestockRepository restockRepository;
    private final CatalogService catalogService;
    private final SupplierService supplierService;
    private final StockService stockService;
    private final CashflowService cashflowService;

    public RestockService(RestockRepository restockRepository, CatalogService catalogService,
                           SupplierService supplierService, StockService stockService, CashflowService cashflowService) {
        this.restockRepository = restockRepository;
        this.catalogService = catalogService;
        this.supplierService = supplierService;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public RestockOperation registerRestock(String id, String productId, String supplierId, int quantity, double unitCost) {
        Product product = catalogService.getProductOrThrow(productId);
        Supplier supplier = supplierService.getSupplierOrThrow(supplierId);
        RestockOperation operation = new RestockOperation(id, product, supplier, quantity, unitCost);
        stockService.increaseStock(productId, quantity);
        cashflowService.recordOutflow("Restock " + id + " for product " + productId, operation.getTotalCost());
        restockRepository.add(operation);
        return operation;
    }

    public List<RestockOperation> listAll() {
        return restockRepository.findAll();
    }
}