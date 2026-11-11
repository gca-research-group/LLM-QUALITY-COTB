package com.store.people;

public class Customer {
    private final String id;
    private final String name;
    private final LoyaltyTier loyaltyTier;

    public Customer(String id, String name, LoyaltyTier loyaltyTier) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Customer id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (loyaltyTier == null) {
            throw new IllegalArgumentException("Loyalty tier cannot be null");
        }
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }
}