package com.store.service;

import com.store.exception.InvalidAmountException;
import com.store.model.CashflowEntry;
import com.store.model.CashflowType;
import com.store.repository.CashflowRepository;

import java.util.Collection;

public class CashflowService {
    private final CashflowRepository cashflowRepository;
    private int counter;

    public CashflowService(CashflowRepository cashflowRepository) {
        this.cashflowRepository = cashflowRepository;
        this.counter = 0;
    }

    public CashflowEntry addInflow(double amount, String description) {
        return addEntry(CashflowType.INFLOW, amount, description);
    }

    public CashflowEntry addOutflow(double amount, String description) {
        return addEntry(CashflowType.OUTFLOW, amount, description);
    }

    public CashflowEntry addManualOutflow(double amount, String description) {
        return addOutflow(amount, description);
    }

    private CashflowEntry addEntry(CashflowType type, double amount, String description) {
        if (amount < 0) {
            throw new InvalidAmountException("Cashflow amount cannot be negative");
        }
        counter++;
        CashflowEntry entry = new CashflowEntry("CF" + counter, type, amount, description);
        cashflowRepository.save(entry);
        return entry;
    }

    public double getTotalInflow() {
        double total = 0.0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double getTotalOutflow() {
        double total = 0.0;
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

    public Collection<CashflowEntry> getAllEntries() {
        return cashflowRepository.findAll();
    }
}