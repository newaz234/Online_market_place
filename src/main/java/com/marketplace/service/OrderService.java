package com.marketplace.service;

import com.marketplace.dto.CartItem;
import com.marketplace.entity.*;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Cart checkout
    public List<OrderEntity> checkout(List<CartItem> cartItems, String buyerUsername,
                                      String fullName, String phone, String email,
                                      String address, String city, String postalCode) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + buyerUsername));

        List<OrderEntity> placed = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));

            // Seller নিজের product কিনতে পারবে না
            if (product.getSeller() != null &&
                product.getSeller().getUsername().equals(buyerUsername)) {
                continue;
            }

            // Restricted seller-এর product checkout-এ block
            if (product.getSeller() != null && product.getSeller().isRestricted()) {
                throw new IllegalArgumentException(
                    "Cannot checkout: product '" + product.getName() +
                    "' is unavailable — seller account has been restricted.");
            }

            if (product.getStock() != null && product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for: " + product.getName() +
                        ". Available: " + product.getStock());
            }

            if (product.getStock() != null) {
                product.setStock(product.getStock() - item.getQuantity());
                productRepository.save(product);
            }

            OrderEntity order = new OrderEntity();
            order.setProduct(product);
            order.setBuyer(buyer);
            order.setQuantity(item.getQuantity());
            order.setStatus(OrderStatus.PENDING);
            order.setFullName(fullName);
            order.setPhone(phone);
            order.setEmail(email);
            order.setAddress(address);
            order.setCity(city);
            order.setPostalCode(postalCode);
            placed.add(orderRepository.save(order));
        }
        return placed;
    }

    public Optional<OrderEntity> findById(Long id) { return orderRepository.findById(id); }

    public List<OrderEntity> findByBuyerUsername(String username) {
        return orderRepository.findByBuyerUsername(username);
    }

    public List<OrderEntity> findAll() { return orderRepository.findAll(); }

    public List<OrderEntity> findBySellerUsername(String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return orderRepository.findByProductSeller(seller);
    }

    // Buyer cancel — SHIPPED হওয়ার আগে
    public void buyerCancelOrder(Long orderId, String buyerUsername) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!order.getBuyer().getUsername().equals(buyerUsername)) {
            throw new AccessDeniedException("You can only cancel your own orders.");
        }

        if (order.getStatus() == OrderStatus.SHIPPED ||
            order.getStatus() == OrderStatus.DELIVERED ||
            order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException(
                "Cannot cancel — order status is: " + order.getStatus().name());
        }

        Product product = order.getProduct();
        if (product.getStock() != null)
            product.setStock(product.getStock() + order.getQuantity());
        productRepository.save(product);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // Seller/Admin status update
    public OrderEntity updateStatus(Long orderId, OrderStatus newStatus,
                                    String username, boolean isAdmin) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (!isAdmin) {
            String sellerName = order.getProduct().getSeller() != null
                    ? order.getProduct().getSeller().getUsername() : null;
            if (!username.equals(sellerName)) {
                throw new AccessDeniedException("You can only update orders for your own products.");
            }
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled order.");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public long count() { return orderRepository.count(); }
}
