package com.marketplace.controller;

import com.marketplace.entity.Product;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.service.CartService;
import com.marketplace.service.ProductService;
import com.marketplace.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
public class ProductController {

    private final ProductService service;
    private final CartService cartService;
    private final ReviewService reviewService;

    private static final String UPLOAD_DIR = "uploads/products/";

    public ProductController(ProductService service, CartService cartService,
                             ReviewService reviewService) {
        this.service = service;
        this.cartService = cartService;
        this.reviewService = reviewService;
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    private boolean isSeller(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SELLER"));
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "false") boolean inStockOnly,
            Authentication auth, HttpSession session, Model model) {

        List<Product> products;
        if (search != null && !search.isBlank())           products = service.search(search);
        else if (category != null && !category.isBlank())  products = service.findByCategory(category);
        else if (inStockOnly)                               products = service.findInStock();
        else                                                products = service.findAll();

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        model.addAttribute("category", category);
        model.addAttribute("inStockOnly", inStockOnly);
        model.addAttribute("totalCount", service.count());
        model.addAttribute("cartCount", cartService.getCartCount(session));

        // Restricted seller-এর product IDs — Buy button disable করার জন্য
        List<Long> restrictedProductIds = products.stream()
                .filter(p -> p.getSeller() != null && p.getSeller().isRestricted())
                .map(Product::getId).toList();
        model.addAttribute("restrictedProductIds", restrictedProductIds);

        if (auth != null && !isAdmin(auth) && isSeller(auth)) {
            List<Long> myProductIds = service.findBySeller(auth.getName())
                    .stream().map(Product::getId).toList();
            model.addAttribute("myProductIds", myProductIds);
        }
        return "products";
    }

    // Product detail page — reviews সহ
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id,
                                Authentication auth, HttpSession session, Model model) {
        Product p = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        model.addAttribute("product", p);
        model.addAttribute("reviews", reviewService.getVisibleReviews(id));
        model.addAttribute("avgRating", reviewService.getAverageRating(id));
        model.addAttribute("cartCount", cartService.getCartCount(session));

        // Seller নিজের product review দিতে পারবে না
        boolean isOwnProduct = isSeller(auth) && p.getSeller() != null
                && p.getSeller().getUsername().equals(auth.getName());
        model.addAttribute("canReview", !isOwnProduct);
        return "product-detail";
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @PostMapping("/products")
    public String add(@ModelAttribute Product p,
                      @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                      Authentication auth,
                      RedirectAttributes redirectAttrs) {
        // image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            if (imageUrl != null) p.setImageUrl(imageUrl);
        }
        if (isAdmin(auth)) service.save(p);
        else               service.saveForSeller(p, auth.getName());
        return "redirect:/products";
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @PostMapping("/products/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth) {
        service.deleteById(id, auth.getName(), isAdmin(auth));
        return "redirect:/products";
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @GetMapping("/products/edit/{id}")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        Product p = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!isAdmin(auth) && (p.getSeller() == null ||
                !p.getSeller().getUsername().equals(auth.getName()))) {
            return "redirect:/seller/my-products?error=notYours";
        }
        model.addAttribute("product", p);
        return "edit-product";
    }

    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    @PostMapping("/products/edit/{id}")
    public String editSave(@PathVariable Long id,
                           @ModelAttribute Product p,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           Authentication auth) {
        Product existing = service.findById(id).orElseThrow();
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            if (imageUrl != null) p.setImageUrl(imageUrl);
        } else {
            p.setImageUrl(existing.getImageUrl()); // পুরানো image রাখো
        }
        service.update(id, p, auth.getName(), isAdmin(auth));
        return "redirect:/seller/my-products";
    }

    private String saveImage(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename()
                    .replaceAll("[^a-zA-Z0-9._-]", "_");
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/" + UPLOAD_DIR + filename;
        } catch (IOException e) {
            return null;
        }
    }
}
