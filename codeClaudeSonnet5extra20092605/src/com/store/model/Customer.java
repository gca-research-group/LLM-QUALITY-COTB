package com.store.model;

public class Customer {
    private final String id;
    private final String name;
    private final LoyaltyTier tier;

    public Customer(String id, String name, LoyaltyTier tier) {
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
}