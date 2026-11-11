package store.model;
public record ShippingRule(ShippingType type,double cost){public ShippingRule{if(cost<0||type==null)throw new IllegalArgumentException("Invalid shipping");}public double apply(){return type==ShippingType.PICKUP?0:cost;}}
