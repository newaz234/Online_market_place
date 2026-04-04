package com.marketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User buyer;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Buyer contact/shipping details
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String postalCode;

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product p) { this.product = p; }
    public User getBuyer() { return buyer; }
    public void setBuyer(User u) { this.buyer = u; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus s) { this.status = s; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public String getFullName() { return fullName; }
    public void setFullName(String s) { this.fullName = s; }
    public String getPhone() { return phone; }
    public void setPhone(String s) { this.phone = s; }
    public String getEmail() { return email; }
    public void setEmail(String s) { this.email = s; }
    public String getAddress() { return address; }
    public void setAddress(String s) { this.address = s; }
    public String getCity() { return city; }
    public void setCity(String s) { this.city = s; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String s) { this.postalCode = s; }
}
