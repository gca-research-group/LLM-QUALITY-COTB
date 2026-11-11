package com.store.csv;

import com.store.model.CashflowEntry;
import com.store.model.Product;
import com.store.service.CashflowService;
import com.store.service.CatalogService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {
    private final CatalogService catalogService;
    private final CashflowService cashflowService;

    public CsvExporter(CatalogService catalogService, CashflowService cashflowService) {
        this.catalogService = catalogService;
        this.cashflowService = cashflowService;
    }

    public String exportInventory(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("productId,name,category,unitPrice,stockQuantity,active");
            List<Product> products = catalogService.allProducts();
            for (Product product : products) {
                writer.println(product.getId() + ","
                        + product.getName() + ","
                        + product.getCategory().getId() + ","
                        + product.getUnitPrice() + ","
                        + product.getStockQuantity() + ","
                        + product.isActive());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export inventory CSV file: " + fileName, e);
        }
        return fileName;
    }

    public String exportCashflow(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("entryId,type,amount,description");
            List<CashflowEntry> entries = cashflowService.allEntries();
            for (CashflowEntry entry : entries) {
                writer.println(entry.getId() + ","
                        + entry.getType() + ","
                        + entry.getAmount() + ","
                        + entry.getDescription());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export cashflow CSV file: " + fileName, e);
        }
        return fileName;
    }
}