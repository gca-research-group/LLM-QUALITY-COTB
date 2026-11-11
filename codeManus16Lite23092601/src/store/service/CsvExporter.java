package store.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import store.model.Transactions.CashflowEntry;
import store.repository.DataStore;

public final class CsvExporter {
    private CsvExporter() {}
    public static void exportInventory(DataStore db, Path path) throws IOException { StringBuilder s = new StringBuilder("product_id,product_name,category,unit_price,stock,active\n"); db.products.values().forEach(p -> s.append(p.getId()).append(',').append(p.getName()).append(',').append(p.getCategory().getName()).append(',').append(p.getUnitPrice()).append(',').append(p.getStock()).append(',').append(p.isActive()).append('\n')); Files.writeString(path, s.toString()); }
    public static void exportCashflow(DataStore db, Path path) throws IOException { StringBuilder s = new StringBuilder("entry_id,type,amount,description\n"); for (CashflowEntry e : db.cashflow) s.append(e.getId()).append(',').append(e.getType()).append(',').append(e.getAmount()).append(',').append(e.getDescription()).append('\n'); Files.writeString(path, s.toString()); }
}
