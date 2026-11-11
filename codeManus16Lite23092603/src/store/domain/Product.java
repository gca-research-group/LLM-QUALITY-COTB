package store.domain;

public class Product {
    private final String id;
    private final String name;
    private final Category category;
    private double unitPrice;
    private boolean active = true;
    private int stock;

    public Product(String id, String name, double unitPrice, Category category) {
        if (id == null || id.isBlank() || name == null || name.isBlank() || unitPrice < 0) throw new IllegalArgumentException("Dados de produto invalidos");
        this.id = id; this.name = name; this.unitPrice = unitPrice; this.category = category;
    }
    public String getId(){ return id; }
    public String getName(){ return name; }
    public Category getCategory(){ return category; }
    public double getUnitPrice(){ return unitPrice; }
    public boolean isActive(){ return active; }
    public int getStock(){ return stock; }
    public void setActive(boolean value){ active = value; }
    public void increaseStock(int quantity){ if(quantity < 0) throw new IllegalArgumentException("Quantidade invalida"); stock += quantity; }
    public void decreaseStock(int quantity){ if(quantity < 0 || quantity > stock) throw new IllegalArgumentException("Estoque insuficiente"); stock -= quantity; }
}
