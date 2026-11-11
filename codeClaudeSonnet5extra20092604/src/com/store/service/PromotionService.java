package com.store.service;

import com.store.promotion.Promotion;

import java.util.ArrayList;
import java.util.List;

public class PromotionService {
    private final List<Promotion> promotions = new ArrayList<Promotion>();

    public void register(Promotion promotion) {
        promotions.add(promotion);
    }

    public List<Promotion> getAll() {
        return new ArrayList<Promotion>(promotions);
    }
}