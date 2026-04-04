package com.marketplace.service;

import com.marketplace.dto.ProductDTO;
import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final UserRepository userRepository;

    public ProductService(ProductRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public ProductDTO toDTO(Product p) {
        return new ProductDTO(p.getId(), p.getName(), p.getPrice(),
                p.getDescription(), p.getCategory(), p.getStock());
    }

    public List<Product> findAll() { return repo.findAll(); }

    public Optional<Product> findById(Long id) { return repo.findById(id); }

    public Product findByIdOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    // Seller নিজের product দেখবে
    public List<Product> findBySeller(String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return repo.findBySeller(seller);
    }

    // Seller হিসেবে product save — seller auto-assign
    public Product saveForSeller(Product p, String sellerUsername) {
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + sellerUsername));
        p.setSeller(seller);
        return repo.save(p);
    }

    // Admin যেকোনো product save করতে পারবে
    public Product save(Product p) { return repo.save(p); }

    // Seller শুধু নিজের product delete করতে পারবে
    public void deleteById(Long id, String username, boolean isAdmin) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!isAdmin && (p.getSeller() == null || !p.getSeller().getUsername().equals(username))) {
            throw new AccessDeniedException("You can only delete your own products.");
        }
        repo.deleteById(id);
    }

    // Seller শুধু নিজের product update করতে পারবে
    public Product update(Long id, Product updated, String username, boolean isAdmin) {
        Product p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        if (!isAdmin && (p.getSeller() == null || !p.getSeller().getUsername().equals(username))) {
            throw new AccessDeniedException("You can only edit your own products.");
        }
        p.setName(updated.getName());
        p.setPrice(updated.getPrice());
        p.setDescription(updated.getDescription());
        p.setCategory(updated.getCategory());
        p.setStock(updated.getStock());
        return repo.save(p);
    }

    public List<Product> search(String query) {
        if (query == null || query.trim().isEmpty()) return repo.findAll();
        return repo.search(query.trim());
    }

    public List<Product> findByCategory(String category) {
        if (category == null || category.isBlank()) return repo.findAll();
        return repo.findByCategory(category);
    }

    public List<Product> findInStock() { return repo.findByStockGreaterThan(0); }

    public long count() { return repo.count(); }
}
