package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest2 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void save_shouldPersistAndReturnProduct() {
        Product p = new Product();
        p.setName("Wireless Mouse");
        p.setPrice(29.99);
        when(repo.save(p)).thenReturn(p);

        Product saved = service.save(p);

        assertNotNull(saved);
        assertEquals("Wireless Mouse", saved.getName());
        verify(repo).save(p);
    }
}
