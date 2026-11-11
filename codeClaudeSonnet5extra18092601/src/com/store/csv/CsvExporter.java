package com.store.csv;

import com.store.cashflow.CashflowEntry;
import com.store.cashflow.CashflowService;
import com.store.catalog.Catalog;
import com.store.catalog.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvExporter {
    private final Catalog catalog;
    private final CashflowService cashflowService;

    public CsvExporter(Catalog catalog, CashflowService cashflowService) {
        this.catalog = catalog;
        this.cashflowService = cashflowService;
    }

    public String exportInventory(String fileName) {
        Path path = Paths.get(fileName);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("productId,name,category,unitPrice,stockQuantity,active");
            for (Product product : catalog.allProducts()) {
                writer.println(product.getId() + "," + product.getName() + "," + product.getCategory().getId()
                        + "," + product.getUnitPrice() + "," + product.getStockQuantity() + "," + product.isActive());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to export inventory CSV: " + e.getMessage(), e);
        }
        return fileName;
    }

    public String exportCashflow(String fileName) {
        Path path = Paths.get(fileName);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println("entryId,type,amount,description");
            for (CashflowEntry entry : cashflowService.all()) {
                writer.println(entry.getId() + "," + entry.getType() + "," + entry.getAmount() + "," + entry.getDescription());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to export cashflow CSV: " + e.getMessage(), e);
        }
        return fileName;
    }
}