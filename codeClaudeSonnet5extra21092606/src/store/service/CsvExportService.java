package store.service;

import store.model.CashflowEntry;
import store.model.Product;
import store.repository.CashflowRepository;
import store.repository.ProductRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CsvExportService {
    private final ProductRepository productRepository;
    private final CashflowRepository cashflowRepository;

    public CsvExportService(ProductRepository productRepository, CashflowRepository cashflowRepository) {
        this.productRepository = productRepository;
        this.cashflowRepository = cashflowRepository;
    }

    public String exportInventory(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("productId,name,category,unitPrice,stockQuantity,active");
            for (Product product : productRepository.findAll()) {
                writer.println(
                        product.getId() + "," +
                                product.getName() + "," +
                                product.getCategory().getId() + "," +
                                product.getUnitPrice() + "," +
                                product.getStockQuantity() + "," +
                                product.isActive()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export inventory CSV: " + e.getMessage(), e);
        }
        return filename;
    }

    public String exportCashflow(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("entryId,type,amount,description");
            for (CashflowEntry entry : cashflowRepository.findAll()) {
                writer.println(
                        entry.getId() + "," +
                                entry.getType() + "," +
                                entry.getAmount() + "," +
                                entry.getDescription()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export cashflow CSV: " + e.getMessage(), e);
        }
        return filename;
    }
}