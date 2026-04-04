package com.marketplace.controller;

import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.Role;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;
import com.marketplace.service.ReviewService;
import com.marketplace.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public AdminController(UserService userService, ProductService productService,
                           OrderService orderService, ReviewService reviewService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var allOrders = orderService.findAll();
        long pending   = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long shipped   = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
        long delivered = allOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        double revenue = allOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(o -> o.getProduct().getPrice() * o.getQuantity()).sum();

        model.addAttribute("userCount", userService.count());
        model.addAttribute("productCount", productService.count());
        model.addAttribute("orderCount", allOrders.size());
        model.addAttribute("buyerCount", userService.countByRole(Role.BUYER));
        model.addAttribute("sellerCount", userService.countByRole(Role.SELLER));
        model.addAttribute("pendingCount", pending);
        model.addAttribute("shippedCount", shipped);
        model.addAttribute("deliveredCount", delivered);
        model.addAttribute("totalRevenue", revenue);
        model.addAttribute("allStatuses", OrderStatus.values());
        return "admin/dashboard";
    }

    // User management tab
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    // Restrict/Unrestrict seller
    @PostMapping("/users/{id}/toggle-restrict")
    public String toggleRestrict(@PathVariable Long id, RedirectAttributes r) {
        try {
            userService.toggleRestrict(id);
        } catch (IllegalArgumentException e) {
            r.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes r) {
        try {
            userService.deleteById(id);
        } catch (IllegalArgumentException e) {
            r.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Orders tab
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        model.addAttribute("allStatuses", OrderStatus.values());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, OrderStatus.valueOf(status.toUpperCase()), "admin", true);
        return "redirect:/admin/orders";
    }

    // Reviews tab
    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/toggle")
    public String toggleReview(@PathVariable Long id) {
        reviewService.toggleVisibility(id);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }

    // Products tab
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id, "admin", true);
        return "redirect:/admin/products";
    }
}
