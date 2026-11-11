package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.Product;
import com.store.model.RestockEntry;
import com.store.model.Supplier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestockService {
    private final Map<String, RestockEntry> entries = new LinkedHashMap<>();
    private final StockService stockService;
    private final CashflowService cashflowService;

    public RestockService(StockService stockService, CashflowService cashflowService) {
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public RestockEntry registerRestock(String restockId, Product product, Supplier supplier, int quantity, double unitCost) {
        if (entries.containsKey(restockId)) {
            throw new BusinessRuleException("Duplicate restock identifier: " + restockId);
        }
        RestockEntry entry = new RestockEntry(restockId, product, supplier, quantity, unitCost);
        entries.put(restockId, entry);
        stockService.increaseStock(product.getId(), quantity);
        cashflowService.recordOutflow("CF-" + restockId, entry.getTotalCost(),
                "Restock " + restockId + " for product " + product.getId() + " from supplier " + supplier.getId());
        return entry;
    }

    public List<RestockEntry> allRestocks() {
        return new ArrayList<>(entries.values());
    }
}