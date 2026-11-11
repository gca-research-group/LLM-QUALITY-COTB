package com.store.service;

import com.store.exception.EntityNotFoundException;
import com.store.promotion.Promotion;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class PromotionService {
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public void register(Promotion promotion) {
        promotions.put(promotion.getName(), promotion);
    }

    public Promotion getByName(String name) {
        Promotion promotion = promotions.get(name);
        if (promotion == null) {
            throw new EntityNotFoundException("Promotion not found: " + name);
        }
        return promotion;
    }

    public Collection<Promotion> getAll() {
        return promotions.values();
    }
}