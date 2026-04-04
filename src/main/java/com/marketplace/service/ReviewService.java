package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.entity.Review;
import com.marketplace.entity.User;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.ReviewRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository,
                         OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public Review addReview(Long productId, String reviewerUsername, int rating, String comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + reviewerUsername));

        // নিজের product-এ review দেওয়া যাবে না
        if (product.getSeller() != null &&
            product.getSeller().getUsername().equals(reviewerUsername)) {
            throw new IllegalArgumentException("You cannot review your own product.");
        }

        // একজন user একটি product-এ একবারই review দিতে পারবে
        if (reviewRepository.existsByProductIdAndReviewerUsername(productId, reviewerUsername)) {
            throw new IllegalArgumentException("You have already reviewed this product.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setReviewer(reviewer);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    public List<Review> getVisibleReviews(Long productId) {
        return reviewRepository.findByProductIdAndVisibleTrue(productId);
    }

    public List<Review> getAllReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void toggleVisibility(Long reviewId) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        r.setVisible(!r.isVisible());
        reviewRepository.save(r);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public double getAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndVisibleTrue(productId);
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }
}
