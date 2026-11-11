package store.model;

public class Category {
    private final String id;
    private final String name;

    public Category(String id, String name) {
        if (id == null || id.length() == 0 || name == null || name.length() == 0) {
            throw new IllegalArgumentException("Invalid category");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}