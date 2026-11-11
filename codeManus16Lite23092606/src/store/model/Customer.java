package store.model;
public record Customer(String id,String name,LoyaltyTier tier){public Customer{if(id==null||name==null||tier==null)throw new IllegalArgumentException("Invalid customer");}}
