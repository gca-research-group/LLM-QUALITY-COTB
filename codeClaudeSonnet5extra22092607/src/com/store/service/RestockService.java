package com.store.service;

import com.store.exception.DuplicateIdException;
import com.store.exception.EntityNotFoundException;
import com.store.model.RestockOperation;
import com.store.repository.ProductRepository;
import com.store.repository.RestockRepository;
import com.store.repository.SupplierRepository;

public class RestockService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final RestockRepository restockRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;

    public RestockService(ProductRepository productRepository, SupplierRepository supplierRepository,
                           RestockRepository restockRepository, StockService stockService, CashflowService cashflowService) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.restockRepository = restockRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public RestockOperation registerRestock(String restockId, String productId, String supplierId, int quantity, double unitCost) {
        if (restockRepository.existsById(restockId)) {
            throw new DuplicateIdException("Duplicate restock id: " + restockId);
        }
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found: " + productId);
        }
        if (!supplierRepository.existsById(supplierId)) {
            throw new EntityNotFoundException("Supplier not found: " + supplierId);
        }
        RestockOperation operation = new RestockOperation(restockId, productId, supplierId, quantity, unitCost);
        stockService.increaseStock(productId, quantity);
        restockRepository.save(operation);
        cashflowService.addOutflow(operation.getTotalCost(),
                "Restock " + restockId + " product " + productId + " from supplier " + supplierId);
        return operation;
    }
}