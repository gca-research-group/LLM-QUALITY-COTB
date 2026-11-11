package com.store.service;

import java.util.ArrayList;
import java.util.List;

import com.store.model.Order;
import com.store.promotion.Promotion;

public class PromotionService {
    private final List<Promotion> promotions = new ArrayList<>();

    public void registerPromotion(Promotion promotion) {
        promotions.add(promotion);
    }

    public Promotion applyBestPromotion(Order order) {
        Promotion best = null;
        double bestDiscount = 0.0;
        for (Promotion promotion : promotions) {
            if (promotion.isApplicable(order)) {
                double discount = promotion.calculateDiscount(order);
                if (discount > bestDiscount) {
                    bestDiscount = discount;
                    best = promotion;
                }
            }
        }
        if (best != null) {
            order.setPromotionDiscount(bestDiscount, best.getDescription());
        } else {
            order.setPromotionDiscount(0.0, "None");
        }
        return best;
    }

    public List<Promotion> findAll() {
        return new ArrayList<>(promotions);
    }
}