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
public class ProductServiceTest8 {

    @Mock ProductRepository repo;
    @Mock UserRepository userRepository;
    @InjectMocks ProductService service;

    @Test
    void search_withValidQuery_shouldCallRepoSearch() {
        Product p = new Product(); p.setName("Gaming Headset");
        when(repo.search("Gaming")).thenReturn(List.of(p));

        List<Product> result = service.search("Gaming");

        assertEquals(1, result.size());
        assertEquals("Gaming Headset", result.get(0).getName());
        verify(repo).search("Gaming");
        verify(repo, never()).findAll();
    }
}
