package com.store.cashflow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CashflowService {
    private final Map<String, CashflowEntry> entries = new LinkedHashMap<>();
    private int entrySequence = 0;

    public CashflowEntry recordInflow(double amount, String description) {
        return recordEntry(CashflowType.INFLOW, amount, description);
    }

    public CashflowEntry recordOutflow(double amount, String description) {
        return recordEntry(CashflowType.OUTFLOW, amount, description);
    }

    private CashflowEntry recordEntry(CashflowType type, double amount, String description) {
        entrySequence++;
        String id = "CF" + entrySequence;
        CashflowEntry entry = new CashflowEntry(id, type, amount, description);
        entries.put(id, entry);
        return entry;
    }

    public double getTotalInflow() {
        double total = 0.0;
        for (CashflowEntry entry : entries.values()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double getTotalOutflow() {
        double total = 0.0;
        for (CashflowEntry entry : entries.values()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double getNetCashflow() {
        return getTotalInflow() - getTotalOutflow();
    }

    public List<CashflowEntry> all() {
        return new ArrayList<>(entries.values());
    }
}