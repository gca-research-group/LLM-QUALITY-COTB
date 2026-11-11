package store.model;
public record BuyXGetYPromotion(String productId,int buy,int free)implements Promotion{public BuyXGetYPromotion{if(buy<=0||free<=0)throw new IllegalArgumentException("Invalid promotion");}public double discount(Order o){return o.items().stream().filter(i->i.product().id().equals(productId)).mapToDouble(i->(i.quantity()/(double)(buy+free))*free*i.unitPrice()).sum();}}
