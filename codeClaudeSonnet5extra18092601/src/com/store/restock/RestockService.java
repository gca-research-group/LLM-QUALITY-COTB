package com.store.restock;

import com.store.cashflow.CashflowService;
import com.store.catalog.StockService;
import com.store.people.SupplierRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestockService {
    private final StockService stockService;
    private final SupplierRepository supplierRepository;
    private final CashflowService cashflowService;
    private final Map<String, RestockEntry> entries = new LinkedHashMap<>();
    private int restockSequence = 0;

    public RestockService(StockService stockService, SupplierRepository supplierRepository, CashflowService cashflowService) {
        this.stockService = stockService;
        this.supplierRepository = supplierRepository;
        this.cashflowService = cashflowService;
    }

    public RestockEntry registerRestock(String productId, String supplierId, int quantity, double unitCost) {
        supplierRepository.findById(supplierId);
        restockSequence++;
        String restockId = "RST" + restockSequence;
        RestockEntry entry = new RestockEntry(restockId, productId, supplierId, quantity, unitCost);
        stockService.increaseStock(productId, quantity);
        entries.put(restockId, entry);
        cashflowService.recordOutflow(entry.getTotalCost(), "Restock " + restockId + " for product " + productId);
        return entry;
    }

    public List<RestockEntry> all() {
        return new ArrayList<>(entries.values());
    }
}