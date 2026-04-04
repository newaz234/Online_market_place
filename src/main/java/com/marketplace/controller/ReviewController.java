package com.marketplace.controller;

import com.marketplace.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public String addReview(@RequestParam Long productId,
                            @RequestParam int rating,
                            @RequestParam String comment,
                            Authentication auth,
                            RedirectAttributes redirectAttrs) {
        try {
            reviewService.addReview(productId, auth.getName(), rating, comment);
            redirectAttrs.addFlashAttribute("reviewSuccess", "Review submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("reviewError", e.getMessage());
        }
        return "redirect:/products/" + productId;
    }
}
