package store.model;
public record Category(String id,String name){public Category{if(id==null||name==null||id.isBlank()||name.isBlank())throw new IllegalArgumentException("Invalid category");}}
