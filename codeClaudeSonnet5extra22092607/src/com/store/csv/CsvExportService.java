package com.store.csv;

import com.store.model.CashflowEntry;
import com.store.model.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class CsvExportService {

    public String exportInventory(String filePath, Collection<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("ProductId,Name,Category,UnitPrice,StockQuantity,Active");
            writer.newLine();
            for (Product product : products) {
                writer.write(product.getId() + "," + escape(product.getName()) + "," + product.getCategory().getId()
                        + "," + product.getUnitPrice() + "," + product.getStockQuantity() + "," + product.isActive());
                writer.newLine();
            }
        }
        return filePath;
    }

    public String exportCashflow(String filePath, Collection<CashflowEntry> entries) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("EntryId,Type,Amount,Description");
            writer.newLine();
            for (CashflowEntry entry : entries) {
                writer.write(entry.getId() + "," + entry.getType() + "," + entry.getAmount() + "," + escape(entry.getDescription()));
                writer.newLine();
            }
        }
        return filePath;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",")) {
            return "\"" + value + "\"";
        }
        return value;
    }
}