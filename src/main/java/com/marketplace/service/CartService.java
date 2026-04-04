package com.marketplace.service;

import com.marketplace.dto.CartItem;
import com.marketplace.entity.Product;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final String CART_KEY = "cart";
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Long productId, int quantity, String currentUsername) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        // Seller নিজের product কিনতে পারবে না
        if (p.getSeller() != null && p.getSeller().getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("You cannot add your own product to cart.");
        }

        // Restricted seller-এর product কেনা যাবে না
        if (p.getSeller() != null && p.getSeller().isRestricted()) {
            throw new IllegalArgumentException(
                "This product is currently unavailable. The seller account has been restricted.");
        }

        if (p.getStock() != null && p.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + p.getStock());
        }

        List<CartItem> cart = getCart(session);
        Optional<CartItem> existing = cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + quantity;
            if (p.getStock() != null && newQty > p.getStock()) {
                throw new IllegalArgumentException("Cannot add more than available stock.");
            }
            existing.get().setQuantity(newQty);
        } else {
            String sellerUsername = p.getSeller() != null ? p.getSeller().getUsername() : null;
            boolean sellerRestricted = p.getSeller() != null && p.getSeller().isRestricted();
            cart.add(new CartItem(productId, p.getName(), p.getPrice(),
                    quantity, p.getStock(), sellerUsername, sellerRestricted));
        }
        session.setAttribute(CART_KEY, cart);
    }

    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(i -> i.getProductId().equals(productId));
        session.setAttribute(CART_KEY, cart);
    }

    public void updateQuantity(HttpSession session, Long productId, int quantity) {
        List<CartItem> cart = getCart(session);
        cart.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(Math.max(1, quantity)));
        session.setAttribute(CART_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_KEY);
    }

    public int getCartCount(HttpSession session) {
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getCartTotal(HttpSession session) {
        return getCart(session).stream().mapToDouble(CartItem::getTotalPrice).sum();
    }
}
