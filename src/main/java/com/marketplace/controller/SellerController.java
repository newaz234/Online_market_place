package com.marketplace.controller;

import com.marketplace.entity.OrderEntity;
import com.marketplace.entity.OrderStatus;
import com.marketplace.entity.Product;
import com.marketplace.service.OrderService;
import com.marketplace.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/seller")
@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
public class SellerController {

    private final OrderService orderService;
    private final ProductService productService;

    public SellerController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    // Seller নিজের product-এ আসা orders দেখবে
    @GetMapping("/orders")
    public String viewOrders(
            @RequestParam(required = false) String status,
            Authentication auth, Model model) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<OrderEntity> orders = isAdmin
                ? orderService.findAll()
                : orderService.findBySellerUsername(auth.getName());

        if (status != null && !status.isBlank()) {
            OrderStatus filterStatus = OrderStatus.valueOf(status.toUpperCase());
            orders = orders.stream().filter(o -> o.getStatus() == filterStatus).toList();
        }

        long pending    = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long processing = orders.stream().filter(o -> o.getStatus() == OrderStatus.PROCESSING).count();
        long shipped    = orders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
        long delivered  = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
        long cancelled  = orders.stream().filter(o -> o.getStatus() == OrderStatus.CANCELLED).count();
        double revenue  = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .mapToDouble(o -> o.getProduct().getPrice() * o.getQuantity()).sum();

        model.addAttribute("orders", orders);
        model.addAttribute("statusFilter", status);
        model.addAttribute("allStatuses", OrderStatus.values());
        model.addAttribute("pendingCount", pending);
        model.addAttribute("processingCount", processing);
        model.addAttribute("shippedCount", shipped);
        model.addAttribute("deliveredCount", delivered);
        model.addAttribute("cancelledCount", cancelled);
        model.addAttribute("revenue", revenue);
        return "seller/orders";
    }

    // Order status update
    @PostMapping("/orders/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        orderService.updateStatus(id, newStatus, auth.getName(), isAdmin);
        return "redirect:/seller/orders";
    }

    // Seller নিজের products দেখবে ও সেখান থেকে edit/delete করবে
    @GetMapping("/my-products")
    public String myProducts(Authentication auth, Model model) {
        List<Product> products = productService.findBySeller(auth.getName());
        model.addAttribute("products", products);
        return "seller/my-products";
    }
}
