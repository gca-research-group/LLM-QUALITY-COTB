package store.domain;

import java.util.*;

public class Cart {
    public record Item(Product product, int quantity) {}
    private final String id; private final Customer customer; private final Map<String, Item> items = new LinkedHashMap<>();
    public Cart(String id, Customer customer){ this.id=id; this.customer=customer; }
    public String getId(){return id;} public Customer getCustomer(){return customer;} public Collection<Item> getItems(){return items.values();}
    public void add(Product p, int quantity){ if(quantity<=0) throw new IllegalArgumentException("Quantidade invalida"); if(!p.isActive()) throw new IllegalArgumentException("Produto inativo"); Item old=items.get(p.getId()); items.put(p.getId(), new Item(p, old==null?quantity:old.quantity()+quantity)); }
    public void update(String productId,int quantity){ if(quantity<=0 || !items.containsKey(productId)) throw new IllegalArgumentException("Item invalido"); Item old=items.get(productId); items.put(productId,new Item(old.product(),quantity)); }
    public void remove(String productId){ items.remove(productId); }
    public double subtotal(){ return items.values().stream().mapToDouble(i->i.product().getUnitPrice()*i.quantity()).sum(); }
}
