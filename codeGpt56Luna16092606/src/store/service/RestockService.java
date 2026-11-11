package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.CashflowEntry;
import store.model.CashflowType;
import store.model.Product;
import store.model.RestockEntry;
import store.model.Supplier;

public class RestockService {
    private final StockService stock;
    private final CashflowService cashflow;
    private final List<RestockEntry> entries = new ArrayList<RestockEntry>();

    public RestockService(StockService stock, CashflowService cashflow) {
        this.stock = stock;
        this.cashflow = cashflow;
    }

    public RestockEntry restock(
        String id,
        Product product,
        Supplier supplier,
        int quantity,
        double unitCost
    ) {
        RestockEntry entry = new RestockEntry(
            id,
            product,
            supplier,
            quantity,
            unitCost
        );

        stock.increase(product, quantity);
        entries.add(entry);

        cashflow.record(new CashflowEntry(
            "RST-" + id,
            CashflowType.OUTFLOW,
            entry.getTotalCost(),
            "Restocking " + product.getId()
        ));

        return entry;
    }

    public List<RestockEntry> all() {
        return entries;
    }
}