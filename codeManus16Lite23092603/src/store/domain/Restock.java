package store.domain;
public record Restock(String id, String productId, String supplierId, int quantity, double unitCost) { public double total(){return quantity*unitCost;} }
