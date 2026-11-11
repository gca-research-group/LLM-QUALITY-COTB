package store.service;

import store.model.CashflowEntry;
import store.model.CashflowType;
import store.repository.CashflowRepository;

import java.util.ArrayList;
import java.util.List;

public class CashflowService {
    private final CashflowRepository cashflowRepository;
    private int counter;

    public CashflowService(CashflowRepository cashflowRepository) {
        this.cashflowRepository = cashflowRepository;
        this.counter = 0;
    }

    private String nextId() {
        counter++;
        return "CASH" + counter;
    }

    public CashflowEntry recordInflow(double amount, String description) {
        CashflowEntry entry = new CashflowEntry(nextId(), CashflowType.INFLOW, amount, description);
        cashflowRepository.add(entry);
        return entry;
    }

    public CashflowEntry recordOutflow(double amount, String description) {
        CashflowEntry entry = new CashflowEntry(nextId(), CashflowType.OUTFLOW, amount, description);
        cashflowRepository.add(entry);
        return entry;
    }

    public double totalInflow() {
        double total = 0;
        for (CashflowEntry entry : cashflowRepository.findAll()) {
            if (entry.getType() == CashflowType.INFLOW) {
                total += entry.getAmount();
            }
        }
        return total;
    }

    public double totalOutflow() {
        double total = 0;
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

    public List<CashflowEntry> allEntries() {
        return new ArrayList<>(cashflowRepository.findAll());
    }
}