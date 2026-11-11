package com.store.shipping;

public interface ShippingRule {
    String getDescription();

    double calculateCost();
}