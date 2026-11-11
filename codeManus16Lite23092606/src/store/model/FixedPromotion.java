package store.model;
public record FixedPromotion(double amount,double threshold,LoyaltyTier tier)implements Promotion{public double discount(Order o){return o.subtotal()>=threshold&&o.customer().tier()==tier?amount:0;}}
