package com.marketplace.dto;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
    private Integer maxStock;
    private String sellerUsername;
    private boolean sellerRestricted;

    public CartItem() {}

    public CartItem(Long productId, String productName, Double price,
                    int quantity, Integer maxStock, String sellerUsername, boolean sellerRestricted) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.maxStock = maxStock;
        this.sellerUsername = sellerUsername;
        this.sellerRestricted = sellerRestricted;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long id) { this.productId = id; }
    public String getProductName() { return productName; }
    public void setProductName(String n) { this.productName = n; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public Integer getMaxStock() { return maxStock; }
    public void setMaxStock(Integer s) { this.maxStock = s; }
    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String s) { this.sellerUsername = s; }
    public boolean isSellerRestricted() { return sellerRestricted; }
    public void setSellerRestricted(boolean r) { this.sellerRestricted = r; }
    public double getTotalPrice() { return price * quantity; }
}
