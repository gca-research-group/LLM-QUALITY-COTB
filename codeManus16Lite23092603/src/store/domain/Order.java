package store.domain;

import java.util.*;

public class Order {
    public record Item(Product product, int quantity, double unitPrice) { public double subtotal(){ return quantity*unitPrice; } }
    private final String id; private final Customer customer; private final List<Item> items; private OrderStatus status=OrderStatus.CREATED; private double discount; private double shipping;
    public Order(String id, Customer customer, List<Item> items){ if(items.isEmpty()) throw new IllegalArgumentException("Pedido vazio"); this.id=id;this.customer=customer;this.items=new ArrayList<>(items); }
    public String getId(){return id;} public Customer getCustomer(){return customer;} public List<Item> getItems(){return Collections.unmodifiableList(items);} public OrderStatus getStatus(){return status;} public double getDiscount(){return discount;} public double getShipping(){return shipping;}
    public double subtotal(){return items.stream().mapToDouble(Item::subtotal).sum();} public void setDiscount(double v){if(v<0)throw new IllegalArgumentException("Desconto invalido");discount=v;} public void setShipping(double v){if(v<0)throw new IllegalArgumentException("Frete invalido");shipping=v;} public double total(){return subtotal()-discount+shipping;}
    public void pay(){if(status==OrderStatus.CANCELLED)throw new IllegalStateException("Pedido cancelado");status=OrderStatus.PAID;} public void cancel(){if(status==OrderStatus.PAID)throw new IllegalStateException("Pedido pago");status=OrderStatus.CANCELLED;} public void markReturned(boolean full){status=full?OrderStatus.REFUNDED:OrderStatus.PARTIALLY_RETURNED;}
}
