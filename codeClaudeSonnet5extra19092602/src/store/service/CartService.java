package store.service;

import store.exception.BusinessRuleException;
import store.model.Cart;
import store.model.Customer;
import store.model.Product;
import store.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;
    private final CatalogService catalogService;
    private final CustomerService customerService;
    private int cartCounter;

    public CartService(CartRepository cartRepository, CatalogService catalogService, CustomerService customerService) {
        this.cartRepository = cartRepository;
        this.catalogService = catalogService;
        this.customerService = customerService;
        this.cartCounter = 0;
    }

    public Cart createCart(String customerId) {
        Customer customer = customerService.findCustomer(customerId);
        cartCounter++;
        String cartId = "CART" + cartCounter;
        Cart cart = new Cart(cartId, customer);
        cartRepository.add(cart);
        return cart;
    }

    public Cart findCart(String cartId) {
        Cart cart = cartRepository.findById(cartId);
        if (cart == null) {
            throw new BusinessRuleException("Cart not found: " + cartId);
        }
        return cart;
    }

    public void addProduct(String cartId, String productId, int quantity) {
        Cart cart = findCart(cartId);
        Product product = catalogService.findProduct(productId);
        cart.addProduct(product, quantity);
    }

    public void updateQuantity(String cartId, String productId, int quantity) {
        Cart cart = findCart(cartId);
        cart.updateQuantity(productId, quantity);
    }

    public void removeProduct(String cartId, String productId) {
        Cart cart = findCart(cartId);
        cart.removeProduct(productId);
    }

    public double getSubtotal(String cartId) {
        return findCart(cartId).getSubtotal();
    }
}