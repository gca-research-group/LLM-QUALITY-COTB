package store.csv;

import store.model.CashflowEntry;
import store.model.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {

    public String exportInventory(List<Product> products, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("productId,name,category,unitPrice,stock,active");
            for (Product product : products) {
                writer.println(product.getId() + "," + product.getName() + "," + product.getCategory().getId()
                        + "," + product.getUnitPrice() + "," + product.getStockQuantity() + "," + product.isActive());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export inventory CSV: " + e.getMessage(), e);
        }
        return filename;
    }

    public String exportCashflow(List<CashflowEntry> entries, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("id,type,amount,description");
            for (CashflowEntry entry : entries) {
                writer.println(entry.getId() + "," + entry.getType() + "," + entry.getAmount() + "," + entry.getDescription());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export cashflow CSV: " + e.getMessage(), e);
        }
        return filename;
    }
}