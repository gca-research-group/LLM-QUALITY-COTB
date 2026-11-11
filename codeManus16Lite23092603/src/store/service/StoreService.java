package store.service;

import store.domain.*;
import store.repository.InMemoryStore;
import java.io.*;
import java.util.*;

public class StoreService {
    private final InMemoryStore db;
    public StoreService(InMemoryStore db){this.db=db;}
    public void addCategory(Category c){db.unique(db.categories,c.id(),"Categoria");db.categories.put(c.id(),c);}
    public void addProduct(Product p){db.unique(db.products,p.getId(),"Produto");db.products.put(p.getId(),p);}
    public void addCustomer(Customer c){db.unique(db.customers,c.id(),"Cliente");db.customers.put(c.id(),c);}
    public void addSupplier(Supplier s){db.unique(db.suppliers,s.id(),"Fornecedor");db.suppliers.put(s.id(),s);}
    public Product product(String id){Product p=db.products.get(id);if(p==null)throw new IllegalArgumentException("Produto inexistente");return p;}
    public Customer customer(String id){Customer c=db.customers.get(id);if(c==null)throw new IllegalArgumentException("Cliente inexistente");return c;}
    public Cart createCart(String id,String customerId){db.unique(db.carts,id,"Carrinho");Cart c=new Cart(id,customer(customerId));db.carts.put(id,c);return c;}
    public void addToCart(String cartId,String productId,int qty){db.carts.get(cartId).add(product(productId),qty);}
    public void updateCart(String cartId,String productId,int qty){db.carts.get(cartId).update(productId,qty);}
    public void removeFromCart(String cartId,String productId){db.carts.get(cartId).remove(productId);}
    public Order createOrder(String id,String cartId){Cart c=db.carts.get(cartId);List<Order.Item> list=new ArrayList<>();for(Cart.Item i:c.getItems()){i.product().decreaseStock(i.quantity());list.add(new Order.Item(i.product(),i.quantity(),i.product().getUnitPrice()));}Order o=new Order(id,c.getCustomer(),list);db.orders.put(id,o);return o;}
    public void cancel(String id){Order o=db.orders.get(id);o.cancel();for(Order.Item i:o.getItems())product(i.product().getId()).increaseStock(i.quantity());}
    public void pay(String paymentId,String orderId,PaymentMethod method){Order o=db.orders.get(orderId);o.pay();double amount=o.total();db.payments.add(new Payment(paymentId,orderId,amount,method));cash(paymentId,CashflowType.INFLOW,amount,"Pagamento "+orderId);}
    public ReturnRequest processReturn(String id,String orderId,Map<String,Integer> items){Order o=db.orders.get(orderId);if(o.getStatus()!=OrderStatus.PAID)throw new IllegalStateException("Pedido nao pode ser devolvido");double total=0;Map<String,Integer> copy=new LinkedHashMap<>();for(Map.Entry<String,Integer> e:items.entrySet()){int bought=o.getItems().stream().filter(i->i.product().getId().equals(e.getKey())).mapToInt(Order.Item::quantity).sum();if(e.getValue()<=0||e.getValue()>bought)throw new IllegalArgumentException("Quantidade devolvida invalida");Product p=product(e.getKey());p.increaseStock(e.getValue());total+=o.getItems().stream().filter(i->i.product().getId().equals(e.getKey())).findFirst().orElseThrow().unitPrice()*e.getValue();copy.put(e.getKey(),e.getValue());}ReturnRequest r=new ReturnRequest(id,orderId,copy,total);db.returns.add(r);o.markReturned(copy.values().stream().mapToInt(Integer::intValue).sum()==o.getItems().stream().mapToInt(Order.Item::quantity).sum());cash(id,CashflowType.OUTFLOW,total,"Reembolso "+orderId);return r;}
    public Restock restock(String id,String productId,String supplierId,int qty,double unitCost){if(qty<=0||unitCost<0)throw new IllegalArgumentException("Reabastecimento invalido");if(!db.suppliers.containsKey(supplierId))throw new IllegalArgumentException("Fornecedor inexistente");product(productId).increaseStock(qty);Restock r=new Restock(id,productId,supplierId,qty,unitCost);db.restocks.add(r);cash(id,CashflowType.OUTFLOW,r.total(),"Reabastecimento "+productId);return r;}
    public void cash(String id,CashflowType type,double amount,String description){if(amount<0)throw new IllegalArgumentException("Valor invalido");db.cashflow.add(new CashflowEntry(id,type,amount,description));}
    public void exportCsv(String file,boolean inventory)throws IOException{try(PrintWriter w=new PrintWriter(new FileWriter(file))){if(inventory){w.println("productId,name,stock");for(Product p:db.products.values())w.printf("%s,%s,%d%n",p.getId(),p.getName(),p.getStock());}else{w.println("id,type,amount,description");for(CashflowEntry e:db.cashflow)w.printf("%s,%s,%.2f,%s%n",e.id(),e.type(),e.amount(),e.description());}}}
    public InMemoryStore db(){return db;}
}
