package com.marketplace.service;

import com.marketplace.entity.Product;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest11 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void findByCategory_shouldReturnMatchingProducts() {
        Product p = new Product(); p.setName("iPhone"); p.setCategory("Electronics");
        when(repo.findByCategory("Electronics")).thenReturn(List.of(p));

        List<Product> result = service.findByCategory("Electronics");

        assertEquals(1, result.size());
        assertEquals("iPhone", result.get(0).getName());
        verify(repo).findByCategory("Electronics");
        verify(repo, never()).findAll();
    }
}
