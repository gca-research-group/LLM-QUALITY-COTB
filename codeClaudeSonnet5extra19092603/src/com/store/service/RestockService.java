package com.store.service;

import java.util.Collection;

import com.store.model.Product;
import com.store.model.RestockEntry;
import com.store.model.Supplier;
import com.store.repository.RestockRepository;

public class RestockService {
    private final RestockRepository restockRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private int sequence = 0;

    public RestockService(RestockRepository restockRepository, StockService stockService,
                           CashflowService cashflowService) {
        this.restockRepository = restockRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
    }

    public RestockEntry registerRestock(Product product, Supplier supplier, int quantity, double unitCost) {
        sequence++;
        String id = "RST" + sequence;
        RestockEntry entry = new RestockEntry(id, product, supplier, quantity, unitCost);
        stockService.increaseStock(product.getId(), quantity);
        cashflowService.registerOutflow(entry.getTotalCost(), "Restock " + id + " for product " + product.getId());
        restockRepository.add(entry);
        return entry;
    }

    public Collection<RestockEntry> findAll() {
        return restockRepository.findAll();
    }
}