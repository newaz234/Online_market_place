package com.example.Software.controller;

import com.marketplace.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String registered,
                            Authentication auth, Model model) {
        // Already logged in হলে redirect
        if (auth != null && auth.isAuthenticated()) {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/products";
        }
        if (error != null)      model.addAttribute("error", "Invalid username or password.");
        if (logout != null)     model.addAttribute("message", "You have been logged out.");
        if (registered != null) model.addAttribute("registered", true);
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) return "redirect:/products";
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam(defaultValue = "BUYER") String role,
                                 Model model) {
        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Username cannot be empty.");
            return "register";
        }
        if (password == null || password.length() < 4) {
            model.addAttribute("error", "Password must be at least 4 characters.");
            return "register";
        }
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Username already taken.");
            return "register";
        }
        userService.register(username, password, role);
        return "redirect:/login?registered=true";
    }

    // Admin login-এর পরে dashboard-এ redirect করার জন্য
    @GetMapping("/")
    public String home(Authentication auth) {
        if (auth != null && auth.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/products";
    }
}
