package com.marketplace.repository;

import com.marketplace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    List<Review> findByProductIdAndVisibleTrue(Long productId);
    List<Review> findByReviewerUsername(String username);
    boolean existsByProductIdAndReviewerUsername(Long productId, String username);
}
