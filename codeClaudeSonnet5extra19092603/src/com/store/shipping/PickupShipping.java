package com.store.shipping;

public class PickupShipping implements ShippingRule {
    @Override
    public String getDescription() {
        return "PICKUP";
    }

    @Override
    public double calculateCost() {
        return 0.0;
    }
}