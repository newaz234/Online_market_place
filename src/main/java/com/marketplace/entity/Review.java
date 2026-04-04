package com.marketplace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private int rating; // 1–5
    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean visible = true; // admin hide করতে পারবে

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product p) { this.product = p; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User u) { this.reviewer = u; }
    public int getRating() { return rating; }
    public void setRating(int r) { this.rating = r; }
    public String getComment() { return comment; }
    public void setComment(String c) { this.comment = c; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean v) { this.visible = v; }
}
