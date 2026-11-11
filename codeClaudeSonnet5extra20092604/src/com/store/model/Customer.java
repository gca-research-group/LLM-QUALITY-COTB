package com.store.model;

import com.store.exception.StoreOperationException;

public class Customer {
    private final String id;
    private final String name;
    private final LoyaltyTier tier;

    public Customer(String id, String name, LoyaltyTier tier) {
        if (id == null) {
            throw new StoreOperationException("Customer id cannot be null");
        }
        if (name == null) {
            throw new StoreOperationException("Customer name cannot be null");
        }
        if (tier == null) {
            throw new StoreOperationException("Customer loyalty tier cannot be null");
        }
        this.id = id;
        this.name = name;
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LoyaltyTier getTier() {
        return tier;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + tier + ")";
    }
}