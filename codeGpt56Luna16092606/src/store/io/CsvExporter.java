package store.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import store.model.CashflowEntry;
import store.model.Product;

public class CsvExporter {
    public void exportInventory(
        java.util.Collection<Product> products,
        String filename
    ) throws IOException {
        PrintWriter out = new PrintWriter(
            new FileWriter(filename)
        );

        try {
            out.println(
                "productId,name,category,stock,unitPrice,active"
            );

            for (Product product : products) {
                out.printf(
                    "%s,%s,%s,%d,%.2f,%s%n",
                    product.getId(),
                    product.getName(),
                    product.getCategory().getId(),
                    product.getStock(),
                    product.getUnitPrice(),
                    product.isActive()
                );
            }
        } finally {
            out.close();
        }
    }

    public void exportCashflow(
        java.util.Collection<CashflowEntry> entries,
        String filename
    ) throws IOException {
        PrintWriter out = new PrintWriter(
            new FileWriter(filename)
        );

        try {
            out.println(
                "id,type,amount,description"
            );

            for (CashflowEntry entry : entries) {
                out.printf(
                    "%s,%s,%.2f,%s%n",
                    entry.getId(),
                    entry.getType(),
                    entry.getAmount(),
                    entry.getDescription()
                );
            }
        } finally {
            out.close();
        }
    }
}