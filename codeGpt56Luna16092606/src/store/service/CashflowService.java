package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.CashflowEntry;
import store.model.CashflowType;

public class CashflowService {
    private final List<CashflowEntry> entries = new ArrayList<CashflowEntry>();

    public void record(CashflowEntry entry) {
        entries.add(entry);
    }

    public void manualOutflow(String id, double amount, String description) {
        record(new CashflowEntry(
            id,
            CashflowType.OUTFLOW,
            amount,
            description
        ));
    }

    public double totalInflow() {
        double total = 0;

        for (CashflowEntry entry : entries) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }

        return total;
    }

    public double totalOutflow() {
        double total = 0;

        for (CashflowEntry entry : entries) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }

        return total;
    }

    public double net() {
        return totalInflow() - totalOutflow();
    }

    public List<CashflowEntry> entries() {
        return entries;
    }
}