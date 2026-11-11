package store.service;

import store.exception.BusinessRuleException;
import store.model.CashflowEntry;
import store.model.CashflowType;
import store.repository.CashflowRepository;

import java.util.List;

public class CashflowService {
    private final CashflowRepository cashflowRepository;
    private int entryCounter;

    public CashflowService(CashflowRepository cashflowRepository) {
        this.cashflowRepository = cashflowRepository;
        this.entryCounter = 0;
    }

    public CashflowEntry recordInflow(double amount, String description) {
        return recordEntry(CashflowType.INFLOW, amount, description);
    }

    public CashflowEntry recordOutflow(double amount, String description) {
        return recordEntry(CashflowType.OUTFLOW, amount, description);
    }

    private CashflowEntry recordEntry(CashflowType type, double amount, String description) {
        if (amount < 0) {
            throw new BusinessRuleException("Cashflow amount cannot be negative");
        }
        entryCounter++;
        String id = "CASH" + entryCounter;
        CashflowEntry entry = new CashflowEntry(id, type, amount, description);
        cashflowRepository.add(entry);
        return entry;
    }

    public List<CashflowEntry> findAllEntries() {
        return cashflowRepository.findAll();
    }

    public double totalInflow() {
        double total = 0.0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double totalOutflow() {
        double total = 0.0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.OUTFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double netCashflow() {
        return totalInflow() - totalOutflow();
    }
}