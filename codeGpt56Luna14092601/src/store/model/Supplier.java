package store.model;

public class Supplier {
    private final String id;
    private final String name;

    public Supplier(String id, String name) {
        if (id == null || id.isBlank() || name == null || name.isBlank()) throw new IllegalArgumentException("Supplier data is invalid");
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}