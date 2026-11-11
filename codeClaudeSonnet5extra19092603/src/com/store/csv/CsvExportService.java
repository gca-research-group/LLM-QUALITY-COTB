package com.store.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.store.exception.InvalidOperationException;
import com.store.model.CashflowEntry;
import com.store.model.Product;
import com.store.service.CashflowService;
import com.store.service.CatalogService;

public class CsvExportService {
    private final CatalogService catalogService;
    private final CashflowService cashflowService;

    public CsvExportService(CatalogService catalogService, CashflowService cashflowService) {
        this.catalogService = catalogService;
        this.cashflowService = cashflowService;
    }

    public String exportInventory(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("productId,name,category,unitPrice,stockQuantity,active");
            for (Product product : catalogService.findAllProducts()) {
                writer.println(product.getId() + "," + product.getName() + "," + product.getCategory().getId() + ","
                        + product.getUnitPrice() + "," + product.getStockQuantity() + "," + product.isActive());
            }
        } catch (IOException e) {
            throw new InvalidOperationException("Failed to export inventory CSV: " + e.getMessage());
        }
        return fileName;
    }

    public String exportCashflow(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,type,amount,description");
            for (CashflowEntry entry : cashflowService.findAll()) {
                writer.println(entry.getId() + "," + entry.getType() + "," + entry.getAmount() + "," + entry.getDescription());
            }
        } catch (IOException e) {
            throw new InvalidOperationException("Failed to export cashflow CSV: " + e.getMessage());
        }
        return fileName;
    }
}