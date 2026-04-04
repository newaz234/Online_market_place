package com.marketplace.controller;

import com.marketplace.dto.CartItem;
import com.marketplace.service.CartService;
import com.marketplace.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = cartService.getCart(session);
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session,
                            Authentication auth,
                            RedirectAttributes redirectAttrs) {
        try {
            cartService.addToCart(session, productId, quantity, auth.getName());
            redirectAttrs.addFlashAttribute("cartSuccess", "Added to cart!");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("cartError", e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session) {
        cartService.removeFromCart(session, productId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQty(@RequestParam Long productId,
                            @RequestParam int quantity,
                            HttpSession session) {
        cartService.updateQuantity(session, productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String fullName,
                             @RequestParam String phone,
                             @RequestParam String email,
                             @RequestParam String address,
                             @RequestParam String city,
                             @RequestParam String postalCode,
                             HttpSession session,
                             Authentication auth,
                             RedirectAttributes redirectAttrs) {
        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) return "redirect:/cart";

        try {
            orderService.checkout(cart, auth.getName(),
                    fullName, phone, email, address, city, postalCode);
            cartService.clearCart(session);
            redirectAttrs.addFlashAttribute("orderSuccess",
                    "Order placed successfully! We'll contact you soon.");
            return "redirect:/orders";
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("checkoutError", e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
}
