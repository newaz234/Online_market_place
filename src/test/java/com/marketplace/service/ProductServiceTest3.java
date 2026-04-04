package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest3 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void findById_shouldReturnProduct_whenExists() {
        Product p = new Product();
        p.setName("Keyboard");
        p.setPrice(59.0);
        when(repo.findById(1L)).thenReturn(Optional.of(p));

        Optional<Product> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Keyboard", result.get().getName());
    }
}
