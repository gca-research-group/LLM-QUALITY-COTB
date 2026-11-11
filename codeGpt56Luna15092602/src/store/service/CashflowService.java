package store.service;

import store.model.CashflowEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CashflowService {
    private final List<CashflowEntry> entries = new ArrayList<>();
    private int sequence;

    public void inflow(double amount, String description) {
        entries.add(new CashflowEntry(
                "CF" + (++sequence),
                CashflowEntry.Type.INFLOW,
                amount,
                description
        ));
    }

    public void outflow(double amount, String description) {
        entries.add(new CashflowEntry(
                "CF" + (++sequence),
                CashflowEntry.Type.OUTFLOW,
                amount,
                description
        ));
    }

    public void manualOutflow(double amount, String description) {
        outflow(amount, description);
    }

    public List<CashflowEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public double totalInflow() {
        return entries.stream()
                .filter(entry -> entry.getType() == CashflowEntry.Type.INFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public double totalOutflow() {
        return entries.stream()
                .filter(entry -> entry.getType() == CashflowEntry.Type.OUTFLOW)
                .mapToDouble(CashflowEntry::getAmount)
                .sum();
    }

    public double net() {
        return totalInflow() - totalOutflow();
    }

    public void exportCsv(String file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,type,amount,description");

            for (CashflowEntry entry : entries) {
                writer.printf(
                        Locale.US,
                        "%s,%s,%.2f,%s%n",
                        entry.getId(),
                        entry.getType(),
                        entry.getAmount(),
                        entry.getDescription().replace(',', ';')
                );
            }
        }
    }
}