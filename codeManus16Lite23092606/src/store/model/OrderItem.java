package store.model;
public record OrderItem(Product product,int quantity,double unitPrice){public double subtotal(){return quantity*unitPrice;}}
