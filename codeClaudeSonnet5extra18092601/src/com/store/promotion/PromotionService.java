package com.store.promotion;

import com.store.order.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PromotionService {
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public void registerPromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null");
        }
        if (promotions.containsKey(promotion.getId())) {
            throw new IllegalArgumentException("Duplicate promotion id: " + promotion.getId());
        }
        promotions.put(promotion.getId(), promotion);
    }

    public Promotion findById(String promotionId) {
        Promotion promotion = promotions.get(promotionId);
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion not found: " + promotionId);
        }
        return promotion;
    }

    public void applyPromotion(String promotionId, Order order) {
        Promotion promotion = findById(promotionId);
        if (!promotion.isApplicable(order)) {
            throw new IllegalStateException("Promotion conditions not satisfied: " + promotionId);
        }
        double discount = promotion.calculateDiscount(order);
        order.applyPromotion(discount, promotion.getDescription());
    }

    public List<Promotion> all() {
        return new ArrayList<>(promotions.values());
    }
}