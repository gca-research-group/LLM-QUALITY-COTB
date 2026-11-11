package com.store.repository;

import java.util.ArrayList;
import java.util.List;

import com.store.model.CashflowEntry;

public class CashflowRepository {
    private final List<CashflowEntry> entries = new ArrayList<>();

    public void add(CashflowEntry entry) {
        entries.add(entry);
    }

    public List<CashflowEntry> findAll() {
        return new ArrayList<>(entries);
    }
}