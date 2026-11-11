package store.model;
public record Restock(String id,Product product,Supplier supplier,int quantity,double unitCost){public Restock{if(quantity<=0||unitCost<0)throw new IllegalArgumentException("Invalid restock");}public double total(){return quantity*unitCost;}}
