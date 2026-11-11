package store.model;
public record CartItem(Product product,int quantity){public CartItem{if(quantity<=0)throw new IllegalArgumentException("Quantity must be positive");}public double subtotal(){return quantity*product.unitPrice();}}
