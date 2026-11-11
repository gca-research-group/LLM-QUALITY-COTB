package store.io;

import java.io.*;
import java.nio.file.*;
import store.service.StoreService;
import store.model.StoreTypes.*;

public final class CsvExporter {
    private CsvExporter() {}
    public static void inventory(StoreService s, Path path) throws IOException { try (BufferedWriter w = Files.newBufferedWriter(path)) { w.write("product_id,name,category,unit_price,stock,active\n"); for (Product p : s.products()) w.write(csv(p.getId()) + "," + csv(p.getName()) + "," + csv(p.getCategory().getName()) + "," + p.getUnitPrice() + "," + p.getStock() + "," + p.isActive() + "\n"); } }
    public static void cashflow(StoreService s, Path path) throws IOException { try (BufferedWriter w = Files.newBufferedWriter(path)) { w.write("entry_id,type,amount,description\n"); for (CashflowEntry e : s.cashflow()) w.write(csv(e.getId()) + "," + e.getType() + "," + e.getAmount() + "," + csv(e.getDescription()) + "\n"); } }
    private static String csv(String value) { return "\"" + value.replace("\"", "\"\"") + "\""; }
}
