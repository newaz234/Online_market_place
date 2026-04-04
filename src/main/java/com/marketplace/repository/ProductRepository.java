package com.marketplace.repository;

import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Product> search(@Param("q") String query);

    List<Product> findByCategory(String category);
    List<Product> findByStockGreaterThan(int stock);

    // Seller-specific
    List<Product> findBySeller(User seller);
    boolean existsByIdAndSeller(Long id, User seller);
}
