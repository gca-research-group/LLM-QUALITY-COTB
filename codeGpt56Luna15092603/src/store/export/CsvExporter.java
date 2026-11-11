package store.export;

import store.model.CashflowEntry;
import store.model.Product;
import store.repository.StoreRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class CsvExporter {
    public static void exportInventory(StoreRepository repository, String filename) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename))) {
            writer.write("productId,name,category,unitPrice,stock,active");
            writer.newLine();

            for (Product product : repository.getCatalog().products()) {
                writer.write(String.join(
                        ",",
                        product.getId(),
                        product.getName(),
                        product.getCategory().getName(),
                        String.format(Locale.US, "%.2f", product.getUnitPrice()),
                        String.valueOf(repository.stock(product.getId())),
                        String.valueOf(product.isActive())
                ));
                writer.newLine();
            }
        }
    }

    public static void exportCashflow(StoreRepository repository, String filename) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filename))) {
            writer.write("id,type,amount,description,timestamp");
            writer.newLine();

            for (CashflowEntry entry : repository.cashflows()) {
                writer.write(String.join(
                        ",",
                        entry.getId(),
                        entry.getType().toString(),
                        String.format(Locale.US, "%.2f", entry.getAmount()),
                        entry.getDescription(),
                        entry.getTimestamp().toString()
                ));
                writer.newLine();
            }
        }
    }
}