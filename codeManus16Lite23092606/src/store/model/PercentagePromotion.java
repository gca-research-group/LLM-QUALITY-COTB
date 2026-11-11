package store.model;
public record PercentagePromotion(double rate,double threshold)implements Promotion{public double discount(Order o){return o.subtotal()>=threshold?o.subtotal()*rate:0;}}
