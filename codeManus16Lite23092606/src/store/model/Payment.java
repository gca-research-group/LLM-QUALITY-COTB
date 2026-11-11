package store.model;
public record Payment(String id,String orderId,double amount,PaymentMethod method){public Payment{if(amount<0)throw new IllegalArgumentException("Negative payment");}}
