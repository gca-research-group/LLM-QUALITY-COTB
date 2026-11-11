package store.model;
public record Supplier(String id,String name){public Supplier{if(id==null||name==null)throw new IllegalArgumentException("Invalid supplier");}}
