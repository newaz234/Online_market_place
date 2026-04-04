package com.marketplace.repository;

import com.marketplace.entity.OrderEntity;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByBuyer(User buyer);
    List<OrderEntity> findByBuyerUsername(String username);

    // Seller-এর product-এ আসা orders
    @Query("SELECT o FROM OrderEntity o WHERE o.product.seller = :seller")
    List<OrderEntity> findByProductSeller(@Param("seller") User seller);

    // Seller-এর product-এ আসা orders — status filter সহ
    @Query("SELECT o FROM OrderEntity o WHERE o.product.seller = :seller AND o.status = :status")
    List<OrderEntity> findByProductSellerAndStatus(@Param("seller") User seller, @Param("status") OrderStatus status);
}
