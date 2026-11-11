package store.repository;

import store.model.*;
import java.util.*;

public final class Repositories {
    public final Map<String,Customer> customers=new LinkedHashMap<>();
    public final Map<String,Supplier> suppliers=new LinkedHashMap<>();
    public final Map<String,Cart> carts=new LinkedHashMap<>();
    public final Map<String,Order> orders=new LinkedHashMap<>();
    public final Map<String,Payment> payments=new LinkedHashMap<>();
    public final Map<String,ReturnRequest> returns=new LinkedHashMap<>();
    public final Map<String,Restock> restocks=new LinkedHashMap<>();
    public final List<CashflowEntry> cashflow=new ArrayList<>();
    public void customer(Customer c){if(customers.putIfAbsent(c.id(),c)!=null)throw new IllegalArgumentException("Duplicate customer");}
    public void supplier(Supplier s){if(suppliers.putIfAbsent(s.id(),s)!=null)throw new IllegalArgumentException("Duplicate supplier");}
}
