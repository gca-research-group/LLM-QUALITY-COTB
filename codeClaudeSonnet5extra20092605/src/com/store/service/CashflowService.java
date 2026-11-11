package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.model.CashflowEntry;
import com.store.model.CashflowType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CashflowService {
    private final Map<String, CashflowEntry> entries = new LinkedHashMap<>();

    public CashflowEntry recordInflow(String id, double amount, String description) {
        if (entries.containsKey(id)) {
            throw new BusinessRuleException("Duplicate cashflow entry identifier: " + id);
        }
        CashflowEntry entry = new CashflowEntry(id, CashflowType.INFLOW, amount, description);
        entries.put(id, entry);
        return entry;
    }

    public CashflowEntry recordOutflow(String id, double amount, String description) {
        if (entries.containsKey(id)) {
            throw new BusinessRuleException("Duplicate cashflow entry identifier: " + id);
        }
        CashflowEntry entry = new CashflowEntry(id, CashflowType.OUTFLOW, amount, description);
        entries.put(id, entry);
        return entry;
    }

    public double totalInflow() {
        double total = 0.0;
        for (CashflowEntry entry : entries.values()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double totalOutflow() {
        double total = 0.0;
        for (CashflowEntry entry : entries.values()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double netCashflow() {
        return totalInflow() - totalOutflow();
    }

    public List<CashflowEntry> allEntries() {
        return new ArrayList<>(entries.values());
    }
}