package store.service;

import store.model.Domain;
import java.io.*;

public final class CsvExporter {
    private CsvExporter() { }
    public static void inventory(StoreService s,String file) throws IOException {try(PrintWriter w=new PrintWriter(new FileWriter(file))){w.println("productId,name,stock,active");for(Domain.Product p:s.products.values())w.printf("%s,%s,%d,%s%n",p.getId(),p.getName(),p.getStock(),p.isActive());}}
    public static void cashflow(StoreService s,String file) throws IOException {try(PrintWriter w=new PrintWriter(new FileWriter(file))){w.println("type,description,amount");for(Domain.CashflowEntry e:s.cashflow)w.printf("%s,%s,%.2f%n",e.getType(),e.getDescription(),e.getAmount());}}
}
