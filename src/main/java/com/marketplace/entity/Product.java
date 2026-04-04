package com.marketplace.entity;

import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String description;
    private String category;
    private Integer stock;
    private String imageUrl; // product ছবির URL বা path

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
    public Integer getStock() { return stock; }
    public void setStock(Integer s) { this.stock = s; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String u) { this.imageUrl = u; }
    public User getSeller() { return seller; }
    public void setSeller(User u) { this.seller = u; }
}
