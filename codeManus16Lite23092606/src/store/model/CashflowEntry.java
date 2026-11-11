package store.model;
public record CashflowEntry(String id,CashflowType type,double amount,String description){public CashflowEntry{if(amount<0)throw new IllegalArgumentException("Negative amount");}}
