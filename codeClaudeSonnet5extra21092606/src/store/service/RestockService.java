package store.service;

import store.model.Product;
import store.model.RestockEntry;
import store.model.Supplier;
import store.repository.RestockEntryRepository;

import java.util.ArrayList;
import java.util.List;

public class RestockService {
    private final RestockEntryRepository restockEntryRepository;
    private final StockService stockService;
    private final CashflowService cashflowService;
    private int counter;

    public RestockService(RestockEntryRepository restockEntryRepository, StockService stockService, CashflowService cashflowService) {
        this.restockEntryRepository = restockEntryRepository;
        this.stockService = stockService;
        this.cashflowService = cashflowService;
        this.counter = 0;
    }

    public RestockEntry registerRestock(Product product, Supplier supplier, int quantity, double unitCost) {
        counter++;
        String id = "RESTOCK" + counter;
        RestockEntry entry = new RestockEntry(id, product, supplier, quantity, unitCost);
        stockService.increaseStock(product.getId(), quantity);
        restockEntryRepository.add(entry);
        cashflowService.recordOutflow(entry.getTotalCost(), "Restock of product " + product.getId() + " from supplier " + supplier.getId());
        return entry;
    }

    public List<RestockEntry> allRestockEntries() {
        return new ArrayList<>(restockEntryRepository.findAll());
    }
}