package com.store.repository;

import com.store.cashflow.CashflowEntry;

import java.util.ArrayList;
import java.util.List;

public class CashflowRepository {
    private final List<CashflowEntry> entries = new ArrayList<CashflowEntry>();

    public void add(CashflowEntry entry) {
        entries.add(entry);
    }

    public List<CashflowEntry> findAll() {
        return new ArrayList<CashflowEntry>(entries);
    }
}