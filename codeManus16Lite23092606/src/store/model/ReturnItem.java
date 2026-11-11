package store.model;
public record ReturnItem(Product product,int quantity,double unitPrice){public ReturnItem{if(quantity<=0||unitPrice<0)throw new IllegalArgumentException("Invalid return item");}public double total(){return quantity*unitPrice;}}
