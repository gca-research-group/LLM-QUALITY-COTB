package store.service;

import store.exception.BusinessRuleException;
import store.model.Product;
import store.model.RestockEntry;
import store.model.Supplier;
import store.repository.RestockRepository;

import java.util.List;

public class RestockService {
    private final RestockRepository restockRepository;
    private final CatalogService catalogService;
    private final SupplierService supplierService;
    private final CashflowService cashflowService;
    private int restockCounter;

    public RestockService(RestockRepository restockRepository, CatalogService catalogService,
                           SupplierService supplierService, CashflowService cashflowService) {
        this.restockRepository = restockRepository;
        this.catalogService = catalogService;
        this.supplierService = supplierService;
        this.cashflowService = cashflowService;
        this.restockCounter = 0;
    }

    public RestockEntry registerRestock(String productId, String supplierId, int quantity, double unitCost) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Restock quantity must be positive for product " + productId);
        }
        if (unitCost < 0) {
            throw new BusinessRuleException("Restock unit cost cannot be negative for product " + productId);
        }
        Product product = catalogService.findProduct(productId);
        Supplier supplier = supplierService.findSupplier(supplierId);
        catalogService.increaseStock(productId, quantity);
        restockCounter++;
        String restockId = "RESTOCK" + restockCounter;
        RestockEntry entry = new RestockEntry(restockId, product, supplier, quantity, unitCost);
        restockRepository.add(entry);
        cashflowService.recordOutflow(entry.getTotalCost(), "Restock " + restockId + " for product " + productId);
        return entry;
    }

    public List<RestockEntry> findAllRestocks() {
        return restockRepository.findAll();
    }
}