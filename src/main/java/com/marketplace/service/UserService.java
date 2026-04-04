package com.marketplace.service;

import com.marketplace.entity.Role;
import com.marketplace.entity.User;
import com.marketplace.exception.DuplicateUsernameException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // App start হলে admin account auto-create হবে
    @PostConstruct
    public void createDefaultAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("✅ Default admin created: username=admin, password=admin123");
        }
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public User register(String username, String rawPassword, String roleStr) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username already taken: " + username);
        }
        // Admin role registration সরাসরি করা যাবে না
        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
            if (role == Role.ADMIN) role = Role.BUYER; // force BUYER if someone tries ADMIN
        } catch (Exception e) {
            role = Role.BUYER;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public void deleteById(Long id) {
        User user = findById(id);
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete admin account.");
        }
        userRepository.deleteById(id);
    }

    public void toggleRestrict(Long id) {
        User user = findById(id);
        if (user.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot restrict admin account.");
        }
        user.setRestricted(!user.isRestricted());
        userRepository.save(user);
    }

    public long count() { return userRepository.count(); }
    public long countByRole(Role role) {
        return userRepository.findAll().stream().filter(u -> u.getRole() == role).count();
    }
}
