package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.entity.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest6 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void deleteById_asSeller_shouldThrow_whenNotOwner() {
        User owner = new User(); owner.setUsername("sellerA");
        Product p = new Product(); p.setName("Item"); p.setSeller(owner);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(AccessDeniedException.class,
                () -> service.deleteById(1L, "sellerB", false));

        verify(repo, never()).deleteById(any());
    }
}
