package store.service;

import store.exception.BusinessRuleException;
import store.promotion.Promotion;

import java.util.LinkedHashMap;
import java.util.Map;

public class PromotionService {
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public void registerPromotion(Promotion promotion) {
        promotions.put(promotion.getId(), promotion);
    }

    public Promotion findPromotion(String promotionId) {
        Promotion promotion = promotions.get(promotionId);
        if (promotion == null) {
            throw new BusinessRuleException("Promotion not found: " + promotionId);
        }
        return promotion;
    }
}