package store.service;

import java.util.ArrayList;
import java.util.List;
import store.model.Promotion;

public class PromotionService {
    private final List<Promotion> promotions = new ArrayList<Promotion>();

    public void register(Promotion promotion) {
        promotions.add(promotion);
    }

    public List<Promotion> all() {
        return promotions;
    }
}