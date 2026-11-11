package store.service;

import store.domain.*;

public final class Rules {
    private Rules(){}
    public static double percentage(double subtotal,double threshold,double percent){return subtotal>=threshold?subtotal*percent/100:0;}
    public static double goldFixed(Order order,double threshold,double amount){return order.getCustomer().tier()==LoyaltyTier.GOLD && order.subtotal()>=threshold?amount:0;}
    public static double buyXGetY(Order order,String productId,int buy,int free){return order.getItems().stream().filter(i->i.product().getId().equals(productId)).mapToDouble(i->(i.quantity()/(buy+free))*free*i.unitPrice()).sum();}
    public static double shipping(ShippingType type){return switch(type){case STANDARD->12.0;case EXPRESS->25.0;case PICKUP->0.0;};}
}
