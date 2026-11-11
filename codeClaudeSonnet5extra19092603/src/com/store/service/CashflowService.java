package com.store.service;

import java.util.List;

import com.store.model.CashflowEntry;
import com.store.model.CashflowType;
import com.store.repository.CashflowRepository;

public class CashflowService {
    private final CashflowRepository cashflowRepository;
    private int sequence = 0;

    public CashflowService(CashflowRepository cashflowRepository) {
        this.cashflowRepository = cashflowRepository;
    }

    public CashflowEntry registerInflow(double amount, String description) {
        return register(CashflowType.INFLOW, amount, description);
    }

    public CashflowEntry registerOutflow(double amount, String description) {
        return register(CashflowType.OUTFLOW, amount, description);
    }

    public CashflowEntry registerManualOutflow(double amount, String description) {
        return registerOutflow(amount, description);
    }

    private CashflowEntry register(CashflowType type, double amount, String description) {
        sequence++;
        String id = "CF" + sequence;
        CashflowEntry entry = new CashflowEntry(id, type, amount, description);
        cashflowRepository.add(entry);
        return entry;
    }

    public double getTotalInflow() {
        double sum = 0.0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.INFLOW) {
                sum += entry.getAmount();
            }
        }
        return sum;
    }

    public double getTotalOutflow() {
        double sum = 0.0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                sum += entry.getAmount();
            }
        }
        return sum;
    }

    public double getNetCashflow() {
        return getTotalInflow() - getTotalOutflow();
    }

    public List<CashflowEntry> findAll() {
        return cashflowRepository.findAll();
    }
}