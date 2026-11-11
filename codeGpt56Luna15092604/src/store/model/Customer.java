package store.model;

public class Customer {
    private final String id;
    private final String name;
    private final LoyaltyTier loyaltyTier;

    public Customer(String id, String name, LoyaltyTier loyaltyTier) {
        if (id == null || id.isBlank() || name == null || name.isBlank() || loyaltyTier == null) {
            throw new IllegalArgumentException("Invalid customer");
        }
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LoyaltyTier getLoyaltyTier() { return loyaltyTier; }
}