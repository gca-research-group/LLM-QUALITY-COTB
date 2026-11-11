package com.store.service;

import com.store.exception.BusinessRuleException;
import com.store.promotion.Promotion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PromotionService {
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public void registerPromotion(Promotion promotion) {
        if (promotions.containsKey(promotion.getId())) {
            throw new BusinessRuleException("Duplicate promotion identifier: " + promotion.getId());
        }
        promotions.put(promotion.getId(), promotion);
    }

    public Promotion findById(String promotionId) {
        Promotion promotion = promotions.get(promotionId);
        if (promotion == null) {
            throw new BusinessRuleException("Promotion not found: " + promotionId);
        }
        return promotion;
    }

    public List<Promotion> allPromotions() {
        return new ArrayList<>(promotions.values());
    }
}