package store.io;

import store.domain.Domain;
import store.service.StoreService;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CsvExporter {
    private CsvExporter() {}
    public static void inventory(StoreService store, String filename) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(filename))) {
            w.write("product_id,name,category,unit_price,stock,active\n");
            for (Domain.Product p : store.products().values()) w.write(String.format("%s,%s,%s,%.2f,%d,%s%n", p.getId(), p.getName(), p.getCategory().getName(), p.getUnitPrice(), p.getStock(), p.isActive()));
        }
    }
    public static void cashflow(StoreService store, String filename) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(filename))) {
            w.write("type,description,amount,timestamp\n");
            for (Domain.CashflowEntry e : store.cashflow()) w.write(String.format("%s,%s,%.2f,%s%n", e.getType(), e.getDescription(), e.getAmount(), e.getTimestamp()));
        }
    }
}
