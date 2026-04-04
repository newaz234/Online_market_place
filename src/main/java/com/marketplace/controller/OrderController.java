package com.marketplace.controller;

import com.marketplace.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String myOrders(Authentication auth, Model model) {
        model.addAttribute("orders", orderService.findByBuyerUsername(auth.getName()));
        return "orders";
    }

    // SHIPPED হওয়ার আগে cancel করা যাবে
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        try {
            orderService.buyerCancelOrder(id, auth.getName());
            redirectAttrs.addFlashAttribute("cancelSuccess", "Order cancelled successfully.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("cancelError", e.getMessage());
        }
        return "redirect:/orders";
    }
}
