package store.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import store.model.CashflowEntry;
import store.model.Product;

public class CsvExporter {
    public static String exportInventory(
            String filename,
            List<Product> products,
            Map<String, Integer> stock) throws IOException {

        FileWriter writer = new FileWriter(filename);

        writer.write("productId,name,stock\n");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            Integer quantity = stock.get(product.getId());
            int value = quantity == null ? 0 : quantity.intValue();

            writer.write(
                product.getId() + "," +
                product.getName() + "," +
                value + "\n"
            );
        }

        writer.close();
        return filename;
    }

    public static String exportCashflow(
            String filename,
            List<CashflowEntry> entries) throws IOException {

        FileWriter writer = new FileWriter(filename);

        writer.write("id,type,amount,description\n");

        for (int i = 0; i < entries.size(); i++) {
            CashflowEntry entry = entries.get(i);

            writer.write(
                entry.getId() + "," +
                entry.getType() + "," +
                entry.getAmount() + "," +
                entry.getDescription() + "\n"
            );
        }

        writer.close();
        return filename;
    }
}