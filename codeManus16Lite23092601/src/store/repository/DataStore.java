package store.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.model.Models.Cart;
import store.model.Models.Category;
import store.model.Models.Customer;
import store.model.Models.Product;
import store.model.Models.Supplier;
import store.model.Transactions.CashflowEntry;
import store.model.Transactions.Order;
import store.model.Transactions.Restock;
import store.model.Transactions.ReturnRequest;
import store.model.Transactions.Promotion;

public final class DataStore {
    public final Map<String, Category> categories = new LinkedHashMap<>();
    public final Map<String, Product> products = new LinkedHashMap<>();
    public final Map<String, Customer> customers = new LinkedHashMap<>();
    public final Map<String, Supplier> suppliers = new LinkedHashMap<>();
    public final Map<String, Cart> carts = new LinkedHashMap<>();
    public final Map<String, Order> orders = new LinkedHashMap<>();
    public final Map<String, ReturnRequest> returns = new LinkedHashMap<>();
    public final Map<String, Restock> restocks = new LinkedHashMap<>();
    public final List<CashflowEntry> cashflow = new ArrayList<>();
    public final List<Promotion> promotions = new ArrayList<>();
}
