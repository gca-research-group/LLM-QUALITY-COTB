package com.store.csv;

import com.store.cashflow.CashflowEntry;
import com.store.model.Product;
import com.store.service.CashflowService;
import com.store.service.CatalogService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvExportService {
    private final CatalogService catalogService;
    private final CashflowService cashflowService;

    public CsvExportService(CatalogService catalogService, CashflowService cashflowService) {
        this.catalogService = catalogService;
        this.cashflowService = cashflowService;
    }

    public String exportInventory(String fileName) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
            writer.println("productId,name,category,unitPrice,stock,active");
            for (Product product : catalogService.listAll()) {
                writer.println(product.getId() + "," + escape(product.getName()) + ","
                        + product.getCategory().getId() + "," + product.getUnitPrice() + ","
                        + product.getStock() + "," + product.isActive());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export inventory CSV: " + e.getMessage(), e);
        }
        return fileName;
    }

    public String exportCashflow(String fileName) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
            writer.println("entryId,type,amount,description");
            for (CashflowEntry entry : cashflowService.listAll()) {
                writer.println(entry.getId() + "," + entry.getType() + "," + entry.getAmount() + ","
                        + escape(entry.getDescription()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export cashflow CSV: " + e.getMessage(), e);
        }
        return fileName;
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}