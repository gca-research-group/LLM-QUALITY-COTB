package com.store.service;

import com.store.cashflow.CashflowEntry;
import com.store.cashflow.CashflowType;
import com.store.repository.CashflowRepository;

import java.util.List;

public class CashflowService {
    private final CashflowRepository cashflowRepository;
    private int sequence = 0;

    public CashflowService(CashflowRepository cashflowRepository) {
        this.cashflowRepository = cashflowRepository;
    }

    public CashflowEntry recordInflow(String description, double amount) {
        return record(CashflowType.INFLOW, description, amount);
    }

    public CashflowEntry recordOutflow(String description, double amount) {
        return record(CashflowType.OUTFLOW, description, amount);
    }

    public CashflowEntry recordManualOutflow(String description, double amount) {
        return record(CashflowType.OUTFLOW, description, amount);
    }

    private CashflowEntry record(CashflowType type, String description, double amount) {
        sequence++;
        String id = "CF" + sequence;
        CashflowEntry entry = new CashflowEntry(id, type, amount, description);
        cashflowRepository.add(entry);
        return entry;
    }

    public double getTotalInflow() {
        double total = 0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double getTotalOutflow() {
        double total = 0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double getNetCashflow() {
        return getTotalInflow() - getTotalOutflow();
    }

    public List<CashflowEntry> listAll() {
        return cashflowRepository.findAll();
    }
}