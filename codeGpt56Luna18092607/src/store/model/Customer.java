package store.model;

public class Customer {
    private String id;
    private String name;
    private LoyaltyTier loyaltyTier;

    public Customer(String id, String name, LoyaltyTier loyaltyTier) {
        if (id == null || id.length() == 0 || name == null || name.length() == 0 || loyaltyTier == null) {
            throw new IllegalArgumentException("Invalid customer");
        }
        this.id = id;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }
}