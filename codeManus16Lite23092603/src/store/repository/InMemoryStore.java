package store.repository;

import store.domain.*;
import java.util.*;

public class InMemoryStore {
    public final Map<String,Product> products=new LinkedHashMap<>(); public final Map<String,Category> categories=new LinkedHashMap<>(); public final Map<String,Customer> customers=new LinkedHashMap<>(); public final Map<String,Supplier> suppliers=new LinkedHashMap<>(); public final Map<String,Cart> carts=new LinkedHashMap<>(); public final Map<String,Order> orders=new LinkedHashMap<>(); public final List<Payment> payments=new ArrayList<>(); public final List<ReturnRequest> returns=new ArrayList<>(); public final List<Restock> restocks=new ArrayList<>(); public final List<CashflowEntry> cashflow=new ArrayList<>();
    public void unique(Map<?,?> map,String id,String type){if(map.containsKey(id))throw new IllegalArgumentException(type+" duplicado");}
}
