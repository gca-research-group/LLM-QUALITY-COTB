package store.service;

import store.model.Cart;
import store.model.Customer;
import store.model.Product;
import store.repository.CartRepository;

public class CartService {
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createCart(String cartId, Customer customer) {
        Cart cart = new Cart(cartId, customer);
        cartRepository.add(cart);
        return cart;
    }

    public void addProduct(String cartId, Product product, int quantity) {
        Cart cart = cartRepository.findById(cartId);
        cart.addProduct(product, quantity);
    }

    public void updateQuantity(String cartId, String productId, int quantity) {
        Cart cart = cartRepository.findById(cartId);
        cart.updateQuantity(productId, quantity);
    }

    public void removeProduct(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId);
        cart.removeProduct(productId);
    }

    public double getSubtotal(String cartId) {
        return cartRepository.findById(cartId).getSubtotal();
    }

    public Cart findById(String cartId) {
        return cartRepository.findById(cartId);
    }
}